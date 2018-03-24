package com.rex.app.onmyoji;

import com.rex.app.onmyoji.io.ResoureWriter;
import com.rex.app.onmyoji.runner.MutiThreadRunner;

public class AllotScenario {
	
	public void run() {
		int THREAD_COUNT = 8;
		
		ResoureWriter.getInstance();
		MutiThreadRunner runner = new MutiThreadRunner(THREAD_COUNT);
		Thread[] threads = new Thread[THREAD_COUNT];  
		for (int i = 0; i < THREAD_COUNT; i++) {
			threads[i] = new Thread(runner);
			threads[i].start();
		}
		try {
			for (int i = 0; i < threads.length; i++) {
				threads[i].join();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();  
		} 
		ResoureWriter.close();
	}
}
