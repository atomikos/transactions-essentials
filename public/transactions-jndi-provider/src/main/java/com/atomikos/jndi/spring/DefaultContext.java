package com.atomikos.jndi.spring;

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

public class DefaultContext implements Context {

	private static final Logger LOGGER = LoggerFactory.createLogger(DefaultContext.class);
	private static final String USER_TRANSACTION_NAME = "java:comp/UserTransaction";

	private UserTransactionManager userTransactionManager;

	private static final NameParser nameParser = new NameParserImpl();

	private final Hashtable<String, Object> environment; // environment for
															// this context

	private String nameInNamespace = "";


	public DefaultContext(Hashtable<String, Object> env) {
		if (env == null) {
			this.environment = new Hashtable<String, Object>();
		} else {
			this.environment = new Hashtable<String, Object>(env);
		}
	}

	@Override
	public Object addToEnvironment(String propName, Object propVal) throws NamingException {
		return environment.put(propName, propVal);
	}

	@Override
	public Hashtable getEnvironment() throws NamingException {
		return (Hashtable) environment.clone();
	}

	public Object removeFromEnvironment(String propName) throws NamingException {
		return environment.remove(propName);
	}

	@Override
	public Object lookup(String s) throws NamingException {
		Object ret;
		if (LOGGER.isDebugEnabled()) {
			LOGGER.logDebug("looking up '" + s + "'");
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
	public Object lookup(Name name) throws NamingException {
		return lookup(name.toString());
	}

	@Override
	public Object lookupLink(String name) throws NamingException {
		return lookup(name);
	}

	@Override
	public Name composeName(Name name, Name prefix) throws NamingException {
		Name result = (Name) prefix.clone();
		result.addAll(name);
		return result;
	}

	@Override
	public String composeName(String name, String prefix) throws NamingException {
		CompositeName result = new CompositeName(prefix);
		result.addAll(new CompositeName(name));
		return result.toString();
	}

	@Override
	public NamingEnumeration<NameClassPair> list(String name) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public NamingEnumeration<Binding> listBindings(String name) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object lookupLink(Name name) throws NamingException {
		return lookupLink(name.toString());
	}

	@Override
	public NamingEnumeration<NameClassPair> list(Name name) throws NamingException {
		return list(name.toString());
	}

	@Override
	public NamingEnumeration<Binding> listBindings(Name name) throws NamingException {
		return listBindings(name.toString());
	}

	@Override
	public void bind(Name name, Object value) throws NamingException {
		bind(name.toString(), value);
	}

	@Override
	public void bind(String name, Object value) throws NamingException {
		IntraVmObjectRegistry.addResource(name, value);
	}

	@Override
	public void close() throws NamingException {
		// ignore
	}

	@Override
	public Context createSubcontext(Name name) throws NamingException {
		throw new OperationNotSupportedException();
	}

	@Override
	public Context createSubcontext(String name) throws NamingException {
		throw new OperationNotSupportedException();
	}

	@Override
	public void destroySubcontext(Name name) throws NamingException {
		throw new OperationNotSupportedException();
	}

	@Override
	public void destroySubcontext(String name) throws NamingException {
		throw new OperationNotSupportedException();
	}

	@Override
	public String getNameInNamespace() throws NamingException {
		return nameInNamespace;
	}

	@Override
	public NameParser getNameParser(Name name) throws NamingException {
		return nameParser;
	}

	@Override
	public NameParser getNameParser(String name) throws NamingException {
		return nameParser;
	}

	@Override
	public void rebind(Name name, Object value) throws NamingException {
		rebind(name.toString(), value);
	}

	@Override
	public void rebind(String name, Object value) throws NamingException {
		IntraVmObjectRegistry.addResource(name, value);
	}

	@Override
	public void rename(Name oldName, Name newName) throws NamingException {
		Object value = lookup(oldName);
		unbind(oldName);
		bind(newName, value);
	}

	@Override
	public void rename(String oldName, String newName) throws NamingException {
		Object value = lookup(oldName);
		unbind(oldName);
		bind(newName, value);
	}

	@Override
	public void unbind(Name name) throws NamingException {
		unbind(name.toString());
	}

	@Override
	public void unbind(String name) throws NamingException {
		IntraVmObjectRegistry.removeResource(name);
	}

	

	private static class NameParserImpl implements NameParser {

		public Name parse(String name) throws NamingException {
			return new CompositeName(name);
		}
	}

}
