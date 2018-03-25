package com.rex.app.onmyoji;

import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.StringUtils;

import com.rex.app.onmyoji.io.DefaultResourceLoader;
import com.rex.app.onmyoji.io.ResoureWriter;
import com.rex.app.onmyoji.nigimitama.NgAbAddition;
import com.rex.app.onmyoji.nigimitama.Nigimitama;
import com.rex.app.onmyoji.nigimitama.SuiteNg;
import com.rex.app.onmyoji.runner.MutiThreadRunner;
import com.rex.app.onmyoji.strategy.StrategyUtill;

public class NgAllocation {
	private static final Logger log = LoggerFactory.getLogger("ng");
	
	public List<SuiteNg> allocate() {
		long recTime = System.currentTimeMillis();
		log.info("线程启动，分配id：{}", MutiThreadRunner.tlc.get());;

		
		List<SuiteNg> stList = doAllocate();

		log.info("线程ID:{} 处理耗时：{}",Thread.currentThread().getId(),
				+ (System.currentTimeMillis() - recTime) / 1000 + "s");

		return stList;
	}

	private List<SuiteNg> doAllocate( ) {
		List<SuiteNg> stList = new ArrayList<SuiteNg>();
		doSubAllocate(null, stList, 0);
		return stList;
	}
	
	private void doSubAllocate(
			List<Nigimitama> ngResult, List<SuiteNg> stList, int index) {
		List<ArrayList<Nigimitama>> ngAllList = DefaultResourceLoader.load();
		if (ngResult == null) {
			ngResult = new ArrayList<Nigimitama>();
		}
		if (index < ngAllList.size()) {
			for (Nigimitama ng : ngAllList.get(index)) {
				if (!StrategyUtill.doStrategy(ng)) { // 剪枝策略
					continue;
				}
				ngResult.add(ng);
				doSubAllocate(ngResult, stList, index + 1);
			}
		} else {
			SuiteNg suiteNg = computeSuite(ngResult);
			regSuite(suiteNg);
		}

		if (ngResult.size() >= 1) {
			ngResult.remove(ngResult.size() - 1);
		}
	}

	private SuiteNg computeSuite(List<Nigimitama> ngResult) {
		/**
		 * 计算属性加成
		 */
		NgAbAddition stabad = new NgAbAddition(); // 套装属性
		BeanWrapper stabadbw = new BeanWrapperImpl(stabad);
		Map<String, Integer> sttpMap = new HashMap<String, Integer>();
		for (Nigimitama ng : ngResult) {
			BeanWrapper ngabadbw = new BeanWrapperImpl(ng.getNgAbAddition());
			for (PropertyDescriptor ngpd : ngabadbw
					.getPropertyDescriptors()) {
				String pdnm = ngpd.getName();
				if (ngpd.getPropertyType().isAssignableFrom(float.class)) {
					stabadbw.setPropertyValue(
							pdnm,
							BigDecimal.valueOf((Float) stabadbw.getPropertyValue(pdnm)
									+ (Float) ngabadbw
									.getPropertyValue(pdnm)).setScale(2,  BigDecimal.ROUND_HALF_UP).floatValue());
				}
			}
	
			String tp = ng.getType();
			if (sttpMap.containsKey(tp)) {
				sttpMap.put(tp, sttpMap.get(tp) + 1);
			} else {
				sttpMap.put(tp, 1);
	
			}
		}
		/**
		 * 计算套装类型及套装加成
		 */
		String sttp = "";
		for (Map.Entry<String, Integer> entry : sttpMap.entrySet()) {
			Integer ngcnt = entry.getValue();
			String ngtp = entry.getKey();
			if (ngcnt >= 2) { // TODO
				if (ngtp.equals("z") || ngtp.equals("xy")) {
					stabad.setAttackRate(BigDecimal
							.valueOf(stabad.getAttackRate() + 0.15f)
							.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
				} else {
					stabad.setCriticalStrikeRate(BigDecimal
							.valueOf(stabad.getCriticalStrikeRate() + 0.15f)
							.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
				}
			}
			if (ngcnt >= 4) { // 确定类型
				sttp = ngtp;
			}
		}
		
		SuiteNg suiteNg = new SuiteNg();
		suiteNg.setStType(sttp); // 套装类型
		suiteNg.setStAbAddition(stabad);
		suiteNg.setNgAlocateList(ngResult); // 套装方案
		return suiteNg;
	}

	private void regSuite(SuiteNg suiteNg) {
		/**
		 * TODO 临时策略
		 */
		if (StringUtils.hasText(suiteNg.getStType())
				&& suiteNg.getStAbAddition().getCriticalStrikeRate() > 0.8f
				&& 3000 * (1 + suiteNg.getStAbAddition().getAttackRate())
						+ suiteNg.getStAbAddition().getAttack() > 8000f) { // 需要有四件套
		// stList.add(suiteNg);
			
			StringBuffer sb = new StringBuffer();
			sb.append(suiteNg.getStType()+",");
			for (Nigimitama ng :suiteNg.getNgAlocateList()) {
				sb.append("|"+ng.getSeqNo());
			}
			sb.append("|," + suiteNg.getStAbAddition().getAttack() + "," + 
					suiteNg.getStAbAddition().getHp() + "," + 
					suiteNg.getStAbAddition().getDefend() + "," + 
					suiteNg.getStAbAddition().getSpeed() + "," + 
					suiteNg.getStAbAddition().getAttackRate() + "," + 
					suiteNg.getStAbAddition().getLifeRate() + "," + 
					suiteNg.getStAbAddition().getDefendRate() + "," + 
					suiteNg.getStAbAddition().getCriticalStrikeRate() + "," + 
					suiteNg.getStAbAddition().getCriticalInjureRate() + "," + 
					suiteNg.getStAbAddition().getEffectHitRate() + "," + 
					suiteNg.getStAbAddition().getEffectResistRate()+"\n");
			System.out.println(sb.toString());
			ResoureWriter.getInstance().writeReport(sb);
		}
	}

}
