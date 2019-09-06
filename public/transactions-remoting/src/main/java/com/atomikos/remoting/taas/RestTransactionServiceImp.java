/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.remoting.taas;

import javax.annotation.PostConstruct;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.Extent;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.Propagation;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.config.Configuration;
import com.atomikos.icatch.jta.TransactionManagerImp;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.remoting.Parser;

public class RestTransactionServiceImp implements RestTransactionService {

	private static Logger LOGGER = LoggerFactory.createLogger(RestTransactionServiceImp.class);

	private String recoveryDomainName;

	@PostConstruct
	public void init() {
		Configuration.init();
		recoveryDomainName = Configuration.getConfigProperties().getTmUniqueName();
	}

	@Override
	public String begin(Long timeout) {
		CompositeTransactionManager compositeTransactionManager = Configuration.getCompositeTransactionManager();
		if (compositeTransactionManager == null) {
			throw new IllegalStateException("Transaction service not initialized !!!!");
		}
		if (timeout == null) {
		    throw new IllegalArgumentException("Missing required argument: timeout");
		}
		
		assertNoTransactionForThread(compositeTransactionManager);

		CompositeTransaction root = compositeTransactionManager.createCompositeTransaction(timeout);
		compositeTransactionManager.suspend();
		
		if (root == null) {
			throw new IllegalStateException("No transaction started");
		}
		
		TransactionManagerImp.markAsJtaTransaction(root);
		
		Propagation p = new Propagation(recoveryDomainName, root, root, root.isSerial(), root.getTimeout());
		LOGGER.logDebug("Returning propagation: " + p.toString());
		return p.toString();
	}

    private void assertNoTransactionForThread(CompositeTransactionManager compositeTransactionManager) {
        CompositeTransaction existingTransaction = compositeTransactionManager.getCompositeTransaction();
		if (existingTransaction != null) {
		    LOGGER.logWarning("Found unexpected existing transaction: " + existingTransaction.getTid() + " rolling it back...");
		    rollback(existingTransaction);
		}
    }

	private void rollback(CompositeTransaction existingTransaction) {
        try {
            existingTransaction.rollback();
        } catch (Exception e) {
            LOGGER.logWarning("Unexpected error during rollback of pending transaction", e);
        }
    }

    Parser parser = new Parser();

	@Override
	public void commit(String... extentsAsString)
			throws RollbackException, HeurMixedException, HeurHazardException, RollbackException {
		CompositeTransactionManager compositeTransactionManager = Configuration.getCompositeTransactionManager();
		if (compositeTransactionManager == null) {
			throw new IllegalStateException("Transaction service not initialized !!!!");
		}
	    
		assertNoTransactionForThread(compositeTransactionManager);

		Extent extent = null;
		try {
			extent = parseExtents(extentsAsString);
		} catch (IllegalArgumentException e) {
			LOGGER.logWarning(e.getMessage());
			throw new RollbackException(e.getMessage());
		}

		CompositeTransaction ct = compositeTransactionManager.getCompositeTransaction(extent.getParentTransactionId());
		ct.getExtent().add(extent);
		try {
			ct.commit();
		} catch (RuntimeException e) {
			LOGGER.logWarning("Unexpected exception on commit: " + e);
			throw e;
		}
	}

	private Extent parseExtents(String[] extents) {
		Extent extent = null;
		String parentTransactionId = null;
		for (int i = 0; i < extents.length; i++) {
			Extent parsed = parser.parseExtent(extents[i]);
			if (i==0) {
			    parentTransactionId = parsed.getParentTransactionId();
				extent = new Extent(parentTransactionId);
			} else {
			    if (!parentTransactionId.equals(parsed.getParentTransactionId())) {
			        throw new IllegalArgumentException("The supplied extents are for different parent transactions");
			    }
			}
			extent.add(parsed);				
		}
		return extent;
	}

	@Override
	public void rollback(String... extentsAsString)
			throws HeurRollbackException, HeurMixedException, HeurHazardException, RollbackException {
		CompositeTransactionManager compositeTransactionManager = Configuration.getCompositeTransactionManager();
		if (compositeTransactionManager == null) {
			throw new IllegalStateException("Transaction service not initialized !!!!");
		}
		
	    assertNoTransactionForThread(compositeTransactionManager);
		
		Extent extent = null;
		try {
			extent = parseExtents(extentsAsString);
		} catch (IllegalArgumentException e) {
			LOGGER.logWarning(e.getMessage());
			throw new RollbackException(e.getMessage());
		}

		CompositeTransaction ct = compositeTransactionManager.getCompositeTransaction(extent.getParentTransactionId());
		try {
			ct.rollback();
		} catch (RuntimeException e) {
			LOGGER.logWarning("Unexpected exception on rollback: " + e);
		}

	}
}
