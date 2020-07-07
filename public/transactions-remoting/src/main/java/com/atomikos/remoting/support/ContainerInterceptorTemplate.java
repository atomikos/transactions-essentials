/**
 * Copyright (C) 2000-2020 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.remoting.support;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.Extent;
import com.atomikos.icatch.ImportingTransactionManager;
import com.atomikos.icatch.Propagation;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.config.Configuration;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.remoting.DefaultImportingTransactionManager;
import com.atomikos.remoting.Parser;

/**
 * Common logic for server-side filters.
 *
 */

public class ContainerInterceptorTemplate {
	
	private static final Logger LOGGER = LoggerFactory.createLogger(ContainerInterceptorTemplate.class);
	private ImportingTransactionManager importingTransactionManager = new DefaultImportingTransactionManager();

	private Parser parser = new Parser();
	/**
	 * Does nothing if propagation is null
	 * @param propagation
	 * @throws IllegalArgumentException if propagation is not understood
	 */
	public void onIncomingRequest(String propagationAsString) throws IllegalArgumentException {
		if (propagationAsString != null) {
			Propagation propagation = parser.parsePropagation(propagationAsString);
			importingTransactionManager.importTransaction(propagation);
		} else {
			LOGGER.logTrace("No transaction context found in incoming request - this request will commit independently of any client transaction!");
		}
	}

	/**
	 * Terminates an imported transaction.
	 * 
	 * @param error If false then rollback, else commit.
	 * @return the extent, or null if no active transaction
	 * @throws RollbackException
	 */
	public String onOutgoingResponse(boolean error) throws RollbackException {
		if (getCurrentTransaction() != null ) {
			try {
				Extent extent = importingTransactionManager.terminated(!error);
				if (extent != null) { // null on error
					return extent.toString();
				}
			} catch (RollbackException e) {
				if (!error) {
					String msg = "Transaction was rolled back - probably due to a timeout?";
					LOGGER.logWarning(msg, e);
					//don't re-throw: no extent will be added so let client detect that
				} else {
					LOGGER.logDebug("Transaction was rolled back after error");
					//don't re-throw: no extent will be added so let client detect that
				}
			}
		}
		return null; // client will fail due to missing extent...
	}

	private CompositeTransaction getCurrentTransaction() {
		return Configuration.getCompositeTransactionManager().getCompositeTransaction();
	}
	
	
	void setImportingTransactionManager(ImportingTransactionManager importingTransactionManager) {
		this.importingTransactionManager = importingTransactionManager;
	}

}
