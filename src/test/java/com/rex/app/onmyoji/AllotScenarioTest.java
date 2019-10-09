package com.rex.app.onmyoji;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;

public class AllotScenarioTest {

	@Test
	public void testRun() {
		// 启动场景类进行测试
		new AllotScenario().run();
		Assert.assertEquals(true, true);
	}

}
