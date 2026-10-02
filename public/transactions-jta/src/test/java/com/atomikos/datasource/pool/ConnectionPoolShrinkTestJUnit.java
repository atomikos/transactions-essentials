/**
 * Copyright (C) 2000-2026 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.datasource.pool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Test;

/**
 * Tests that the pool's maintenance shrink behaviour respects minPoolSize:
 * idle connections beyond maxIdleTime are removed, but the pool never
 * shrinks below minPoolSize, and in-use connections are never reaped.
 *
 * Timing note: connections report a lastTimeReleased far in the past, so
 * each scenario only waits for a maintenance tick (interval 1s) instead of
 * waiting out the idle period itself.
 */
public class ConnectionPoolShrinkTestJUnit {

	private static final long IDLE_SINCE_LONG_AGO = System.currentTimeMillis() - 60 * 60 * 1000L;
	private static final long MAINTENANCE_TICK_TIMEOUT_MILLIS = 5000;
	private static final long EXTRA_TICKS_WAIT_MILLIS = 2500;

	private TestPoolProperties properties = new TestPoolProperties();
	private TestConnectionFactory factory = new TestConnectionFactory();
	private ConnectionPool<Object> pool;

	@After
	public void tearDown() {
		if (pool != null) {
			pool.destroy();
		}
	}

	// cf case 228956: idle connections at/below minPoolSize must never be evicted
	@Test
	public void testIdleConnectionsBelowMinPoolSizeAreNeverEvicted() throws Exception {
		properties.minPoolSize = 2;
		properties.maxPoolSize = 5;
		properties.maxIdleTime = 1;
		pool = new ConnectionPoolWithSynchronizedValidation<Object>(factory, properties);

		growPoolToMaxSize();
		releaseAllConnectionsAsIdleSince(IDLE_SINCE_LONG_AGO);

		waitUntilPoolShrinksTo(properties.minPoolSize);
		assertEquals(properties.minPoolSize, pool.totalSize());
		assertEquals(properties.maxPoolSize - properties.minPoolSize, factory.countDestroyed());

		// all survivors are still idle beyond maxIdleTime: if the shrink bound
		// were broken, subsequent maintenance ticks would drain below min
		Thread.sleep(EXTRA_TICKS_WAIT_MILLIS);
		assertEquals(properties.minPoolSize, pool.totalSize());
	}

	@Test
	public void testInUseConnectionsAreNotRemoved() throws Exception {
		properties.minPoolSize = 1;
		properties.maxPoolSize = 3;
		properties.maxIdleTime = 1;
		pool = new ConnectionPoolWithSynchronizedValidation<Object>(factory, properties);

		growPoolToMaxSize();
		TestXPooledConnection idle = factory.created.get(0);
		idle.release(IDLE_SINCE_LONG_AGO);

		waitUntilPoolShrinksTo(2);
		assertEquals(2, pool.totalSize());
		assertTrue(idle.destroyed);
		assertFalse(factory.created.get(1).destroyed);
		assertFalse(factory.created.get(2).destroyed);
	}

	@Test
	public void testZeroMaxIdleTimeDisablesShrinking() throws Exception {
		properties.minPoolSize = 2;
		properties.maxPoolSize = 5;
		properties.maxIdleTime = 0;
		pool = new ConnectionPoolWithSynchronizedValidation<Object>(factory, properties);

		growPoolToMaxSize();
		releaseAllConnectionsAsIdleSince(IDLE_SINCE_LONG_AGO);

		Thread.sleep(EXTRA_TICKS_WAIT_MILLIS);
		assertEquals(properties.maxPoolSize, pool.totalSize());
	}

	@Test
	public void testRecentlyReleasedConnectionsAreKeptEvenAboveMinPoolSize() throws Exception {
		properties.minPoolSize = 2;
		properties.maxPoolSize = 5;
		properties.maxIdleTime = 1000;
		pool = new ConnectionPoolWithSynchronizedValidation<Object>(factory, properties);

		growPoolToMaxSize();
		releaseAllConnectionsAsIdleSince(System.currentTimeMillis());

		Thread.sleep(EXTRA_TICKS_WAIT_MILLIS);
		assertEquals(properties.maxPoolSize, pool.totalSize());
	}

	private void growPoolToMaxSize() throws Exception {
		for (int i = 0; i < properties.maxPoolSize; i++) {
			pool.borrowConnection();
		}
		assertEquals(properties.maxPoolSize, pool.totalSize());
	}

	private void releaseAllConnectionsAsIdleSince(long releaseTime) {
		for (TestXPooledConnection xpc : factory.created) {
			xpc.release(releaseTime);
		}
	}

	private void waitUntilPoolShrinksTo(int expectedSize) throws InterruptedException {
		long deadline = System.currentTimeMillis() + MAINTENANCE_TICK_TIMEOUT_MILLIS;
		while (pool.totalSize() > expectedSize && System.currentTimeMillis() < deadline) {
			Thread.sleep(50);
		}
	}

	private static class TestConnectionFactory implements ConnectionFactory<Object> {

		// grown connections are created on the pool's executor thread
		private List<TestXPooledConnection> created = Collections.synchronizedList(new ArrayList<TestXPooledConnection>());

		public XPooledConnection<Object> createPooledConnection() {
			TestXPooledConnection ret = new TestXPooledConnection();
			created.add(ret);
			return ret;
		}

		private int countDestroyed() {
			int ret = 0;
			for (TestXPooledConnection xpc : created) {
				if (xpc.destroyed) ret++;
			}
			return ret;
		}
	}

	private static class TestXPooledConnection implements XPooledConnection<Object> {

		private volatile boolean available = true;
		private volatile boolean destroyed = false;
		private volatile long lastTimeReleased = System.currentTimeMillis();
		private final long creationTime = System.currentTimeMillis();
		private final Object proxy = new Object();

		private void release(long releaseTime) {
			lastTimeReleased = releaseTime;
			available = true;
		}

		public boolean isAvailable() {
			return available;
		}

		public boolean canBeRecycledForCallingThread() {
			return false;
		}

		public void destroy() {
			destroyed = true;
		}

		public long getLastTimeAcquired() {
			return creationTime;
		}

		public long getLastTimeReleased() {
			return lastTimeReleased;
		}

		public Object createConnectionProxy() {
			available = false;
			return proxy;
		}

		public boolean isErroneous() {
			return false;
		}

		public long getCreationTime() {
			return creationTime;
		}

		public void registerXPooledConnectionEventListener(XPooledConnectionEventListener<Object> listener) {
			// shrink behaviour does not depend on availability events
		}

		public void unregisterXPooledConnectionEventListener(XPooledConnectionEventListener<Object> listener) {
		}

		public boolean markAsBeingAcquiredIfAvailable() {
			if (available) {
				available = false;
				return true;
			}
			return false;
		}
	}

	private static class TestPoolProperties implements ConnectionPoolProperties {

		private int minPoolSize;
		private int maxPoolSize;
		private int maxIdleTime;

		public String getUniqueResourceName() {
			return "testPool";
		}

		public int getMaxPoolSize() {
			return maxPoolSize;
		}

		public int getMinPoolSize() {
			return minPoolSize;
		}

		public int getBorrowConnectionTimeout() {
			return 5;
		}

		public int getMaxIdleTime() {
			return maxIdleTime;
		}

		public int getMaxLifetime() {
			return 0;
		}

		public int getMaintenanceInterval() {
			return 1;
		}

		public String getTestQuery() {
			return null;
		}

		public boolean getLocalTransactionMode() {
			return false;
		}

		public int getDefaultIsolationLevel() {
			return DEFAULT_ISOLATION_LEVEL_UNSET;
		}

		// kept for byte-identical dev/stable file content across the merge
		// boundary, so the dev->stable merge sees a clean add/add on this new
		// test file instead of a conflict (cf case 228956)
		public boolean getSupportsTmJoin() {
			return false;
		}

		public boolean getUseDriverBasedConnectionValidation() {
			return false;
		}

		public long getConnectionValidationInterval() {
			return 0;
		}
	}
}
