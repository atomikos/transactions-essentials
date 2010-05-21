package com.atomikos.persistence.imp;

import com.atomikos.persistence.StateRecoveryManager;


public class StateRecoveryManagerImpTestJUnit extends
		AbstractJUnitStateRecoveryManagerTest {

	public StateRecoveryManagerImpTestJUnit(String name) {
		super(name);
	}

	protected StateRecoveryManager getInstanceToTest() 
	{
		StateRecoveryManager smgr = null;
		  try {
			String dir = getTemporaryOutputDir() + "/";
			  FileLogStream logs = new FileLogStream ( dir , "teststatelog"  , null );
			  StreamObjectLog log = new StreamObjectLog ( logs , 1 , null );
			  smgr = new StateRecoveryManagerImp ( log );
			  smgr.init();
			 
		} catch (Exception e) {
			failTest ( e.getMessage() );
		}
		 return smgr;
	}

}
