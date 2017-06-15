/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.logging;

import org.junit.Ignore;
import org.junit.Test;

import junit.framework.TestCase;

import static org.junit.Assert.assertEquals;

public class LoggerFactoryTestJUnit {

  @Test
  @Ignore
	public void testCreateLogger() {
		System.out.println(LoggerFactory.loggerFactoryDelegate);

		assertEquals(com.atomikos.logging.Slf4JLoggerFactoryDelegate.class, LoggerFactory.loggerFactoryDelegate.getClass());
	}
}
