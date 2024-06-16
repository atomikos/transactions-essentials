/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.remoting;

import java.util.Stack;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.ExportingTransactionManager;
import com.atomikos.icatch.Extent;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.Propagation;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.config.Configuration;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

/**
 * Default implementation that should work with any remoting protocol as long as
 * Strings can be sent/received.
 *
 */

public class DefaultExportingTransactionManager implements ExportingTransactionManager {

	private static final Logger LOGGER = LoggerFactory.createLogger(DefaultExportingTransactionManager.class);


	@Override
	public Propagation getPropagation() throws SysException, IllegalStateException {
		CompositeTransactionManager ctm = Configuration.getCompositeTransactionManager();
		CompositeTransaction ct = ctm.getCompositeTransaction();
		if (ct == null) {
			throw new IllegalStateException("This method requires a transaction but none was found");
		}
		String recoveryDomainName = Configuration.getConfigProperties().getTmUniqueName();
		String recoveryCoordinatorURI = null;
		CompositeTransaction root = null;
		if (ct.isRoot()) {
			root = ct;
		} else {
			root = ct.getLineage().firstElement();
		}
		Propagation p = new Propagation(recoveryDomainName, root, ct, ct.isSerial(), ct.getTimeout(), recoveryCoordinatorURI);
		LOGGER.logDebug("Exporting propagation: " + p.toString());
		return p;
	}

	
	
	@Override
	public void addExtent(Extent extent) throws SysException, IllegalArgumentException, RollbackException {
		CompositeTransactionManager ctm = Configuration.getCompositeTransactionManager();
		CompositeTransaction ct = ctm.getCompositeTransaction();
		if (ct == null) {
			throw new RollbackException("No transaction found - any remote work will not be committed by us.");
		}
		if (extent == null) {
			throw new IllegalArgumentException("Expected an extent but found none. The remote work will not be committed by us.");
		}
		if (!ct.getTid().equals(extent.getParentTransactionId())) {
		    throw new IllegalArgumentException("The supplied extent is for a different transaction: found " + extent.getParentTransactionId()+ " but expected " + ct.getTid());
		}
		for (Participant p : extent.getParticipants()) {
			String rootId = ct.getCompositeCoordinator().getRootId();
			// detect rccursive call
			if (rootId != null && p.getURI().endsWith(rootId))
				return;
		}
		ct.getExtent().add(extent);
		Stack<Participant> participants = extent.getParticipants();
		for (Participant p : participants) {
			ct.addParticipant(p); //cf case 183884
		}
	}


}
