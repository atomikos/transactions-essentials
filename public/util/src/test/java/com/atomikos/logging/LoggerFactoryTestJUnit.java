/**
 * Copyright (C) 2000-2022 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.logging;

import junit.framework.TestCase;

public class LoggerFactoryTestJUnit extends TestCase {

	public void testCreateLogger() {
		System.out.println(LoggerFactory.loggerFactoryDelegate);

		assertEquals(com.atomikos.logging.Slf4JLoggerFactoryDelegate.class, LoggerFactory.loggerFactoryDelegate.getClass());
	}

}
