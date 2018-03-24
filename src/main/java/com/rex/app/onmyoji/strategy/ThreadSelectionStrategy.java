package com.rex.app.onmyoji.strategy;

import java.util.List;

import com.rex.app.onmyoji.nigimitama.Nigimitama;
import com.rex.app.onmyoji.runner.MutiThreadRunner;

public class ThreadSelectionStrategy implements Strategy {
	
	public boolean doStrategy(Nigimitama nigimitama) {
		List<Nigimitama> runingrsList=MutiThreadRunner.runingrsList;
		int threadNo = MutiThreadRunner.tlc.get();
		
		if (nigimitama.getPosition().equals(
				runingrsList.get(threadNo).getPosition())
				&& (!nigimitama.getSeqNo().equals(
						runingrsList.get(threadNo).getSeqNo()) && !runingrsList
						.get(threadNo).getSeqNo()
						.contains("|" + nigimitama.getSeqNo() + "|"))) {
			return false;
		}

		return true;
	}
}
