package com.rex.app.onmyoji.strategy;

import java.util.ArrayList;
import java.util.List;

import com.rex.app.onmyoji.nigimitama.Nigimitama;

public class StrategyUtill {
	private final static List<Strategy> sltstgList= loadStrategy();

	private StrategyUtill() {
	}

	private static List<Strategy> loadStrategy() {
		List<Strategy> sltstgList = new ArrayList<Strategy>();
		sltstgList.add(new ThreadSelectionStrategy());
		return sltstgList;
	}
	
	public static List<Strategy> getStrategy() {
		return sltstgList;
	}
	
	public static boolean doStrategy(Nigimitama nigimitama) {
		for (Strategy stg:sltstgList) {
			if (!stg.doStrategy(nigimitama)) {
				return false;
			}
		}
		
		return true;
	}
	

}
