/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.imp;

import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RecoveryCoordinator;

class SubTransactionRecoveryCoordinator implements RecoveryCoordinator {

	private static final long serialVersionUID = 1L;
	
	private String superiorCoordinatorId;
	
	public SubTransactionRecoveryCoordinator(String superiorCoordinatorId) {
		this.superiorCoordinatorId = superiorCoordinatorId;
	}
	
	@Override
	public Boolean replayCompletion(Participant participant)
			throws IllegalStateException {
		return null;
	}

	@Override
	public String getURI() {
		return superiorCoordinatorId;
	}

}
