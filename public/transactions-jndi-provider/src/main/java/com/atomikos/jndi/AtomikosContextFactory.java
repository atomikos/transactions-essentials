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
