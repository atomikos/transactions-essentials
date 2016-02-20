/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jndi;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

public class AtomikosContextFactory implements InitialContextFactory {

	@Override
	public Context getInitialContext(Hashtable<?, ?> arg0)
			throws NamingException {
		return new AtomikosContext();
	}

}
