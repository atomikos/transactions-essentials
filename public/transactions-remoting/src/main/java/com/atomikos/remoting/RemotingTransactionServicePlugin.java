/**
 * Copyright (C) 2000-2022 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.remoting;

import com.atomikos.icatch.TransactionServicePlugin;
import com.atomikos.icatch.config.Configuration;
import com.atomikos.icatch.provider.ConfigProperties;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.recovery.fs.RecoveryLogImp;
import com.atomikos.remoting.twopc.AtomikosRestPort;

public class RemotingTransactionServicePlugin implements TransactionServicePlugin {
	
	
	private static final Logger LOGGER = LoggerFactory.createLogger(RemotingTransactionServicePlugin.class);

	private ConfigProperties configProperties;
	
	@Override
	public void beforeInit() {
		configProperties = Configuration.getConfigProperties();
	}

	@Override
	public void afterInit() {
	    AtomikosRestPort.init(findRestPortUrl());
        String registered = null;
        try {
            registered = configProperties.getProperty("com.atomikos.icatch.registered");
        } catch (Exception notRegistered) {
            LOGGER.logTrace("Failed to check for registration property", notRegistered);
        }
        
        if (Configuration.getRecoveryLog() instanceof RecoveryLogImp && registered == null) {
            LOGGER.logWarning("Activating module transactions-remoting - this module is best combined with https://www.atomikos.com/Main/LogCloud for distributed recovery and distributed monitoring...");
        }
	}

	private String findRestPortUrl() {
	    String ret = null;
	    try {
	        ret = configProperties.getProperty(AtomikosRestPort.REST_URL_PROPERTY_NAME);
	    } catch (Exception notFound) {
	        //do nothing: normal if not set in jta.properties
	        //our JAX-RS filter may still set this on the first incoming request
	    }
		return ret;
	}

	@Override
	public void afterShutdown() {
	}

}
