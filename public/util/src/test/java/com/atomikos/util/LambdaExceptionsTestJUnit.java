/**
 * Copyright (C) 2000-2026 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.util;

import static org.junit.Assert.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Test;


public class LambdaExceptionsTestJUnit {
	
	private ExecutorService dynamicallyGrowPoolExecutor = Executors.newFixedThreadPool(1);

	@Test
	public void test() {
		Future<Void> futureXPC = 
				dynamicallyGrowPoolExecutor.submit(() -> {throw new RuntimeException();});
		try {
			futureXPC.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			assertTrue(e.getCause() instanceof RuntimeException);
		}
	}

}
