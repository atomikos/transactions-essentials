/**
 * Copyright (C) 2000-2017 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jndi;

import java.util.Hashtable;

import javax.naming.Binding;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;

import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.util.IntraVmObjectRegistry;

public class AtomikosContext implements Context {

	private static final Logger log = LoggerFactory.createLogger(AtomikosContext.class);
	private static final String USER_TRANSACTION_NAME = "java:comp/UserTransaction";

	private NameParser nameParser = new CompositeNameParser();
	private UserTransactionManager userTransactionManager;

	@Override
	public void close() throws NamingException {
	
	}

	@Override
	public Object lookup(Name name) throws NamingException {
		return lookup(name.toString());
	}

	@Override
	public Object lookup(String s) throws NamingException {
		Object ret;
		if (log.isTraceEnabled()) {
			log.logTrace("looking up '" + s + "'");
		}

		if (USER_TRANSACTION_NAME.equals(s)) {
			if (userTransactionManager == null) {
				userTransactionManager = new UserTransactionManager();
			}
			ret = userTransactionManager;
		} else {
			ret = IntraVmObjectRegistry.getResource(s);
		}
		if (ret == null) {
			throw new NameNotFoundException("unable to find a bound object with name '" + s + "'");
		}
		return ret;
	}

	@Override
	public void bind(Name name, Object o) throws NamingException {
		throw new OperationNotSupportedException();
	}

	@Override
	public void bind(String s, Object o) throws NamingException {
		throw new OperationNotSupportedException();
	}

	@Override
	public void rebind(Name name, Object o) throws NamingException {
		throw new OperationNotSupportedException();
	}

	@Override
	public void rebind(String s, Object o) throws NamingException {
		throw new OperationNotSupportedException();
	}

	@Override
	public void unbind(Name name) throws NamingException {
		throw new OperationNotSupportedException();
	}

	@Override
	public void unbind(String s) throws NamingException {
		throw new OperationNotSupportedException();
	}

	@Override
	public void rename(Name name, Name name1) throws NamingException {
		throw new OperationNotSupportedException();
	}

	@Override
	public void rename(String s, String s1) throws NamingException {
		throw new OperationNotSupportedException();
	}

	@Override
	public NamingEnumeration<NameClassPair> list(Name name) throws NamingException {
		throw new OperationNotSupportedException();
	}

	@Override
	public NamingEnumeration<NameClassPair> list(String s) throws NamingException {
		throw new OperationNotSupportedException();
	}

	@Override
	public NamingEnumeration<Binding> listBindings(Name name) throws NamingException {
		throw new OperationNotSupportedException();
	}

	@Override
	public NamingEnumeration<Binding> listBindings(String s) throws NamingException {
		throw new OperationNotSupportedException();
	}

	@Override
	public void destroySubcontext(Name name) throws NamingException {
		throw new OperationNotSupportedException();
	}

	@Override
	public void destroySubcontext(String s) throws NamingException {
		throw new OperationNotSupportedException();
	}

	@Override
	public Context createSubcontext(Name name) throws NamingException {
		throw new OperationNotSupportedException();
	}

	@Override
	public Context createSubcontext(String s) throws NamingException {
		throw new OperationNotSupportedException();
	}

	@Override
	public Object lookupLink(Name name) throws NamingException {
		throw new OperationNotSupportedException();
	}

	@Override
	public Object lookupLink(String s) throws NamingException {
		throw new OperationNotSupportedException();
	}

	@Override
	public NameParser getNameParser(Name name) throws NamingException {
		return nameParser;
	}

	@Override
	public NameParser getNameParser(String s) throws NamingException {
		return nameParser;
	}

	@Override
	public Name composeName(Name name, Name name1) throws NamingException {
		throw new OperationNotSupportedException();
	}

	@Override
	public String composeName(String s, String s1) throws NamingException {
		throw new OperationNotSupportedException();
	}

	@Override
	public Object addToEnvironment(String s, Object o) throws NamingException {
		throw new OperationNotSupportedException();
	}

	@Override
	public Object removeFromEnvironment(String s) throws NamingException {
		throw new OperationNotSupportedException();
	}

	@Override
	public Hashtable<?, ?> getEnvironment() throws NamingException {
		throw new OperationNotSupportedException();
	}

	@Override
	public String getNameInNamespace() throws NamingException {
		throw new OperationNotSupportedException();
	}

	private final static class CompositeNameParser implements NameParser {

		@Override
		public Name parse(final String name) throws NamingException {
			return new CompositeName(name);
		}
	}

}
