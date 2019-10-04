/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.remoting;

import java.net.URI;
import java.net.URISyntaxException;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.Extent;
import com.atomikos.icatch.ImportingTransactionManager;
import com.atomikos.icatch.Propagation;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.config.Configuration;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.remoting.twopc.ParticipantAdapter;

/**
 * Default implementation that should work with any remoting protocol
 * as long as Strings can be sent/received.
 *
 */

public class DefaultImportingTransactionManager implements ImportingTransactionManager {
	
	private static final Logger LOGGER = LoggerFactory.createLogger(DefaultImportingTransactionManager.class);
	

	private static String atomikosRestPortUrl;
	
	public static void setAtomikosRestPortUrl(String url) {
		atomikosRestPortUrl = url;
	}
	
	private void assertRestPortUrlSet() {
	    if (atomikosRestPortUrl == null) {
	        LOGGER.logFatal("Not configured for remoting - see https://www.atomikos.com/Documentation/ConfiguringRemoting for details");
	    }
 	}
	

	@Override
	public CompositeTransaction importTransaction(Propagation propagation) throws IllegalArgumentException, SysException {
	    assertRestPortUrlSet();
	    if (propagation == null) {
			throw new IllegalArgumentException("Propagation must not be null");
		}
		CompositeTransactionManager ctm = Configuration.getCompositeTransactionManager();
		return ctm.recreateCompositeTransaction(propagation);
	}

	@Override
	public Extent terminated(boolean commit) throws SysException, RollbackException {
		Extent extent = null;
		CompositeTransactionManager ctm = Configuration.getCompositeTransactionManager();
		CompositeTransaction ct = ctm.getCompositeTransaction();
		if(ct != null) {
			try {
				if (commit) {
					extent = ct.getExtent();
					ct.commit();
				} else {
					ct.rollback();
					return null;
				}
				
			} catch (RollbackException rb) {
				throw rb;
			} catch (Throwable e) {
				throw new SysException("Error in termination: " + e.getMessage(), e);
			}
		
		} else {
			throw new RollbackException("Attempting to terminate a transaction that no longer exists - probably due to a timeout?");
		}
		
		URI participantURI;
		try {
			participantURI = new URI(atomikosRestPortUrl+"/"+ct.getCompositeCoordinator().getRootId()+"/" + ct.getCompositeCoordinator().getCoordinatorId());
		} catch (URISyntaxException e) {
			throw new SysException("Could not create URI for extent", e);
		}
		extent.add(new ParticipantAdapter(participantURI), 1);
		LOGGER.logDebug("Returning extent: " + extent);
		return extent;
	}


}
