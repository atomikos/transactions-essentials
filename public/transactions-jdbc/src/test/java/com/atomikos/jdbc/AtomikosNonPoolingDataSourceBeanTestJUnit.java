/**
 * Copyright (C) 2000-2026 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jdbc;

import org.junit.Test;

public class AtomikosNonPoolingDataSourceBeanTestJUnit {

	private AtomikosNonPoolingDataSourceBean sut = new AtomikosNonPoolingDataSourceBean();

	@Test(expected = UnsupportedOperationException.class)
	public void testMaxPoolSizeIsNotSupported() throws Exception {
		sut.setMaxPoolSize(0);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testMinPoolSizeIsNotSupported() throws Exception {
		sut.setMinPoolSize(0);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testPoolSizeIsNotSupported() throws Exception {
		sut.setPoolSize(0);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testMaintenanceIntervalIsNotSupported() throws Exception {
		sut.setMaintenanceInterval(0);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testConcurrentConnectionValidationIsNotSupported() throws Exception {
		sut.setConcurrentConnectionValidation(false);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testMaxIdleTimeIsNotSupported() throws Exception {
		sut.setMaxIdleTime(0);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testMaxLifetimeIsNotSupported() throws Exception {
		sut.setMaxLifetime(0);
	}
	
	@Test
	public void testAssertPoolSizeSettingsDoesNotThrow() throws AtomikosSQLException {
		sut.assertPoolSizeSettings();
	}
}
