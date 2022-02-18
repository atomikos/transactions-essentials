/**
 * Copyright (C) 2000-2022 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jms;

import javax.jms.JMSException;

import com.atomikos.jms.internal.AtomikosJMSException;

import junit.framework.TestCase;

public class AtomikosJMSExceptionTestJUnit extends TestCase {

	
	public void testLinkedException() {
		JMSException linked = new JMSException ( "test" );
		AtomikosJMSException e = new AtomikosJMSException ( getName() );
		e.setLinkedException ( linked );
		assertSame ( linked , e.getLinkedException() );
		assertNull ( e.getCause() );
		assertEquals ( getName() , e.getMessage() );
	}
	
	public void testCause() {
		Exception cause = new Exception ( getName() );
		AtomikosJMSException e = new AtomikosJMSException ( getName() , cause );
		assertSame ( cause , e.getCause() );
		assertSame ( cause , e.getLinkedException() );
		assertEquals ( getName() , e.getMessage() );
	}
	
}
