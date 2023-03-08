package com.atomikos.util;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Before;
import org.junit.Test;

 /**
  * Learning tests for Futures. Kept for later reference.
  *
  */

public class FutureTestJUnit {
	
	private ExecutorService executor = Executors.newFixedThreadPool(1);

	private Future<Integer> future;
	
	@Before
	public void setUp() {
		future = executor.submit(() -> {return 0;});
	}

	@Test(expected=CancellationException.class)
	public void testCancelBeforeGet() throws Exception {
		future.cancel(true);
		future.get();
	}
	
	@Test
	public void testCancelDoesNotAffectNextSubmit() throws Exception {
		future.cancel(true);
		future = executor.submit(() -> {return 0;});
		future.get();
	}
	
	@Test(expected=TimeoutException.class)
	public void testTimeout() throws Exception {
		future = executor.submit(() -> {Thread.currentThread().sleep(100);return 0;});
		future.get(10, TimeUnit.MILLISECONDS);
	}

	@Test
	public void testCancelAfterTimeoutIsAllowed() throws Exception {
		try {
			testTimeout();
		} catch (TimeoutException e) {}
		future.cancel(true);
	}
	
	@Test
	public void testCancelTwiceIsAllowed() throws Exception {
		future.cancel(true);
		future.cancel(true);
	}
	
	@Test
	public void testIsNotCancelledByDefault() throws Exception {
		assertFalse(future.isCancelled());
	}
	
	@Test
	public void testIsCancelledAfterCancel() {
		future.cancel(true);
		assertTrue(future.isCancelled());
	}
	
	@Test
	public void testIsDoneAfterCancel() {
		future.cancel(true);
		assertTrue(future.isDone());
	}
	
	@Test
	public void testIsDoneAfterGet() throws Exception {
		future.get();
		assertTrue(future.isDone());
	}
	
	@Test
	public void testIsNotDoneByDefault() {
		assertFalse(future.isDone());
	}
	
	@Test
	public void testIsNotDoneAfterTimeout() throws Exception {
		try {
			testTimeout();
		} catch (TimeoutException e) {}
		assertFalse(future.isDone());
	}
 }
