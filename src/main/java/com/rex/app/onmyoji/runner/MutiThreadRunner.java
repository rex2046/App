package com.rex.app.onmyoji.runner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.util.CollectionUtils;

import com.rex.app.onmyoji.NgAllocation;
import com.rex.app.onmyoji.io.DefaultResourceLoader;
import com.rex.app.onmyoji.nigimitama.Nigimitama;

public class MutiThreadRunner  implements Runnable {
	
	public static ThreadLocal<Integer> tlc = new ThreadLocal<Integer>();
	private static final AtomicInteger nextId = new AtomicInteger(0);
	public static List<Nigimitama> runingrsList;
	
	public MutiThreadRunner(int threadCount) {
		setRunningResource(DefaultResourceLoader.load(), threadCount);
	}
	
	@Override
	public void run() {
		MutiThreadRunner.tlc.set(nextId.getAndIncrement()); // 分派线程序号
		new NgAllocation().allocate(); // 执行分配
	}
	

	public void setRunningResource(List<ArrayList<Nigimitama>> ngAllList, int threadCount) {
		List<Nigimitama> runrsList = new ArrayList<Nigimitama>();
		List<NgCounter> ngcntList = new ArrayList<NgCounter>(ngAllList.size());
		
		for (int i = 0; i < ngAllList.size(); i++) {
			NgCounter ngcnt = new NgCounter();
			ngcnt.setPos(i+1);
			ngcnt.setCount(ngAllList.get(i).size());
			ngcntList.add(ngcnt);
		}
		Collections.sort(ngcntList);
		
		int count =0;
		for (NgCounter ngCounter:ngcntList) {
			System.out.println("positon:"+ngCounter.getPos()+" count:"+ngCounter.getCount());
			
			count = ngCounter.getCount();
			if (count >= threadCount) {
				System.out.println("positon:"+ngCounter.getPos()+" 满足条件");
				runrsList.addAll(ngAllList.get(ngCounter.getPos()-1));
				break;
			}
		}
		if (CollectionUtils.isEmpty(runrsList)) {
			throw new RuntimeException("无法找到适合多线程匹配");
		}
		String ngpos = runrsList.get(0).getPosition();
		StringBuffer sb = new StringBuffer("|");
		for (int i = runrsList.size() - 1; i >= threadCount - 1 && i >= 0; i--) {
			sb.append(runrsList.remove(i).getSeqNo()+"|");
		}
		Nigimitama nglast = new Nigimitama();
		nglast.setPosition(ngpos);
		nglast.setSeqNo(sb.toString());
		runrsList.add(nglast);
		
		MutiThreadRunner.runingrsList = runrsList;
		
		// test print
		System.out.println("多线程选取结果:");
		for (Nigimitama nigimitama:MutiThreadRunner.runingrsList) {
			System.out.println("positon:"+nigimitama.getPosition()+" seqNo:"+nigimitama.getSeqNo());
		}
	}
	
	private class NgCounter implements Comparable<NgCounter> {
		private int pos;
		private int count;
		
		@Override
		public int compareTo(NgCounter ng) {
			if (this.count >= ng.getCount()) {
				return 1;
			}
			return -1;
		}

		public int getPos() {
			return pos;
		}

		public void setPos(int pos) {
			this.pos = pos;
		}

		public int getCount() {
			return count;
		}

		public void setCount(int count) {
			this.count = count;
		}
	}

}
