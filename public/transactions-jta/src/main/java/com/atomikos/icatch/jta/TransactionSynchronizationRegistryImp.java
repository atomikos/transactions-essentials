package com.atomikos.icatch.jta;

import java.io.Serializable;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

import com.atomikos.icatch.OrderedLifecycleComponent;
import com.atomikos.icatch.SysException;
import com.atomikos.util.SerializableObjectFactory;

public class TransactionSynchronizationRegistryImp 
implements javax.transaction.TransactionSynchronizationRegistry,
Serializable, Referenceable, OrderedLifecycleComponent {


	private static final long serialVersionUID = 1L;
	
	private transient TransactionManager tm;
	
	private void assertTransactionManagerAvailable() {
		tm = TransactionManagerImp.getTransactionManager();
		if (tm == null) {
			throw new IllegalStateException("Transaction service not running");
		}
	}
	
	@Override
	public Object getTransactionKey() {
		assertTransactionManagerAvailable();
		try {
			return tm.getTransaction();
		} catch (SystemException e) {
			throw new SysException(e);
		}
	}

	@Override
	public void putResource(Object key, Object value) {
		assertTransactionManagerAvailable();
		if (key == null) {
			throw new NullPointerException();
		}
		try {
			TransactionImp tx = (TransactionImp) tm.getTransaction();
			if (tx == null) {				
				throwNoTransactionFound();
			} 
			tx.putResource(key, value);
		} catch (SystemException e) {
			throw new SysException(e);
		}
	}

	@Override
	public Object getResource(Object key) {
		assertTransactionManagerAvailable();
		if (key == null) {
			throw new NullPointerException();
		}
		try {
			TransactionImp tx = (TransactionImp) tm.getTransaction();
			if (tx == null) {
				throwNoTransactionFound();
			} 
			return tx.getResource(key);
		} catch (SystemException e) {
			throw new SysException(e);
		}
	}

	@Override
	public void registerInterposedSynchronization(Synchronization sync) {
		assertTransactionManagerAvailable();
		try {
			TransactionImp tx = (TransactionImp) tm.getTransaction();
			if (tx == null) {
				throwNoTransactionFound();
			}
			tx.registerInterposedSynchronization(sync);
		} catch (SystemException e) {
			throw new SysException(e);
		}
	}

	private void throwNoTransactionFound() {
		throw new IllegalStateException("No transaction found for calling thread");
	}

	@Override
	public int getTransactionStatus() {
		assertTransactionManagerAvailable();
		try {
			return tm.getStatus();
		} catch (SystemException e) {
			throw new SysException(e);
		}
	}

	@Override
	public void setRollbackOnly() {
		assertTransactionManagerAvailable();
		try {
			tm.setRollbackOnly();
		} catch (SystemException e) {
			throw new SysException(e);
		}
	}

	@Override
	public boolean getRollbackOnly() {
		assertTransactionManagerAvailable();
		if (getTransactionStatus() == Status.STATUS_NO_TRANSACTION) {
			throwNoTransactionFound();
		}
		return false; // hard to test, be optimistic and return false
	}

	@Override
	public Reference getReference() throws NamingException {
		return SerializableObjectFactory.createReference(this);
	}

	@Override
	public void init() throws Exception {
		//nothing to do: marker interface for this class
	}

	@Override
	public void close() throws Exception {
		//nothing to do: marker interface for this class
	}

}
