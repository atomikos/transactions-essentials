package com.atomikos.persistence.imp;

import com.atomikos.persistence.StateRecoveryManager;

public class VolatileStateRecoveryManagerTestJUnit extends
		AbstractJUnitStateRecoveryManagerTest {

	public VolatileStateRecoveryManagerTestJUnit(String name) {
		super(name);
	}

	protected StateRecoveryManager getInstanceToTest() {
		return new VolatileStateRecoveryManager();
	}

}
