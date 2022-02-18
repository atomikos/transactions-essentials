/**
 * Copyright (C) 2000-2022 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.imp;

import com.atomikos.icatch.RecoveryCoordinator;

class SubTransactionRecoveryCoordinator implements RecoveryCoordinator {

	private String superiorCoordinatorId;
    private String recoveryDomainName;
	
	public SubTransactionRecoveryCoordinator(String superiorCoordinatorId, String recoveryDomainName) {
		this.superiorCoordinatorId = superiorCoordinatorId;
		this.recoveryDomainName = recoveryDomainName;
	}


	@Override
	public String getURI() {
		return superiorCoordinatorId;
	}


    @Override
    public String getRecoveryDomainName() {
        return recoveryDomainName;
    }

}
