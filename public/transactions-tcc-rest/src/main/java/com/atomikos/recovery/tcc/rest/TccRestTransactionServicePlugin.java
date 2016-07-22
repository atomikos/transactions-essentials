package com.atomikos.recovery.tcc.rest;

import java.util.Properties;

import com.atomikos.icatch.TransactionServicePlugin;
import com.atomikos.icatch.config.Configuration;
import com.atomikos.recovery.RecoveryLog;

public class TccRestTransactionServicePlugin implements TransactionServicePlugin {
	
	@Override
	public void beforeInit(Properties properties) {
		Configuration.addResource(new TccRecoverableResource());
	}
	

	@Override
	public void afterInit() {
		RecoveryLog recoveryLog = Configuration.getRecoveryLog();
		if (recoveryLog != null) {
			DefaultTccTransport defaultTccTransport = new DefaultTccTransport();
			TccRecoveryManager.installTccRecoveryManager(recoveryLog, defaultTccTransport);
		}	
	}
	
	@Override
	public void afterShutdown() {
		Configuration.removeResource(TccRecoverableResource.NAME);
	}

}
