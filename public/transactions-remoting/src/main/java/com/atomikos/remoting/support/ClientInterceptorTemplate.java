/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.remoting.support;

import com.atomikos.icatch.ExportingTransactionManager;
import com.atomikos.icatch.Extent;
import com.atomikos.icatch.Propagation;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SysException;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.remoting.CheckedExportingTransactionManager;
import com.atomikos.remoting.DefaultExportingTransactionManager;
import com.atomikos.remoting.Parser;

/**
 * Common logic for client-side filters.
 *
 */

public class ClientInterceptorTemplate {

	private static final Logger LOGGER = LoggerFactory.createLogger(ClientInterceptorTemplate.class);

	private final ExportingTransactionManager exportingTransactionManager = new CheckedExportingTransactionManager(new DefaultExportingTransactionManager());

	private final Parser parser = new Parser();
	/**
	 * Determines the propagation header.
	 * 
	 * @return The header - null if no active transaction exists
	 */
	public String onOutgoingRequest() {
		LOGGER.logTrace("onOutgoingRequest...");
		try {
			Propagation propagation = exportingTransactionManager.getPropagation();
			return propagation.toString();
		} catch (SysException se) {
			LOGGER.logError("System configuration problem - could not retrieve transaction propagation for outgoing request", se);
		} catch (Exception e) {			
			LOGGER.logDebug("Failed to retrieve transaction propagation for outgoing request - request will not be transactional!", e);
		}

		return null;
	}

	/**
	 * Handles the transactional termination of an incoming response.
	 * 
	 * @param extent The extent found in the response headers, can be null.
	 */
	public void onIncomingResponse(String extentAsString) {
		LOGGER.logTrace("onIncomingResponse...");
		Extent extent = null;
		try {
			extent = parser.parseExtent(extentAsString);
			exportingTransactionManager.addExtent(extent);
		} catch (RollbackException e) {
			if (extent != null) {
				String message = "An extent was returned but no local transaction exists - any remote work will time out and rollback.";
				LOGGER.logWarning(message, e);
				//don't rethrow: CheckedExportingTransactionManager will prevent commit anyway
			}
		}  catch (IllegalArgumentException invalidExtent) {
		    String message = "Invalid extent found - any remote work will time out and rollback.";
		    LOGGER.logWarning(message, invalidExtent);
		   //don't rethrow: CheckedExportingTransactionManager will prevent commit anyway
		}

	}

}
