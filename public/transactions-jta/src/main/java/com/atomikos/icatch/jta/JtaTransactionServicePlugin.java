/**
 * Copyright (C) 2000-2017 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.jta;

import java.util.Properties;

import com.atomikos.icatch.TransactionServicePlugin;
import com.atomikos.icatch.config.Configuration;
import com.atomikos.icatch.provider.ConfigProperties;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.recovery.RecoveryLog;
import com.atomikos.recovery.xa.DefaultXaRecoveryLog;
import com.atomikos.recovery.xa.XaResourceRecoveryManager;

public class JtaTransactionServicePlugin implements TransactionServicePlugin {
	
	private static Logger LOGGER = LoggerFactory.createLogger(JtaTransactionServicePlugin.class);
	
	/**
	 * The name of the property indicating whether (JTA/XA) resources should be
	 * registered automatically or not.
	 *
	 * Expands to {@value}.
	 */
	public static final String AUTOMATIC_RESOURCE_REGISTRATION_PROPERTY_NAME = "com.atomikos.icatch.automatic_resource_registration";

	
	/**
	 * The name of the property that specifies the default timeout (in
	 * milliseconds) that is set for transactions when no timeout is specified.
	 *
	 * Expands to {@value}.
	 */
	public static final String DEFAULT_JTA_TIMEOUT_PROPERTY_NAME = "com.atomikos.icatch.default_jta_timeout";

	/**
	 * The name of the property that indicates whether JTA transactions are to
	 * be in serial mode or not.
	 *
	 * Expands to {@value}.
	 */
	public static final String SERIAL_JTA_TRANSACTIONS_PROPERTY_NAME = "com.atomikos.icatch.serial_jta_transactions";

	/**
	 * The name of the property indicating whether remote clients can start
	 * transactions on this service or not.
	 *
	 * Expands to {@value}.
	 */
	public static final String CLIENT_DEMARCATION_PROPERTY_NAME = "com.atomikos.icatch.client_demarcation";

	private boolean autoRegisterResources;


	@Override
	public void beforeInit(Properties properties) {
		ConfigProperties configProperties = new ConfigProperties(properties);
		long defaultTimeoutInMillis = configProperties.getAsLong(DEFAULT_JTA_TIMEOUT_PROPERTY_NAME);
		int defaultJtaTimeout = 0;
		defaultJtaTimeout = (int) defaultTimeoutInMillis/1000;
		if ( defaultJtaTimeout <= 0 ) {
			LOGGER.logWarning ( "WARNING: " + DEFAULT_JTA_TIMEOUT_PROPERTY_NAME + " should be more than 1000 milliseconds - resetting to 10000 milliseconds instead..." );
			defaultJtaTimeout = 10;
		}
		TransactionManagerImp.setDefaultTimeout(defaultJtaTimeout);
		boolean defaultSerial = configProperties.getAsBoolean(SERIAL_JTA_TRANSACTIONS_PROPERTY_NAME);
		TransactionManagerImp.setDefaultSerial(defaultSerial);
		
		boolean clientDemarcation = configProperties.getAsBoolean(CLIENT_DEMARCATION_PROPERTY_NAME);
        if ( clientDemarcation ) {
            String name = configProperties.getTmUniqueName();
            UserTransactionServerImp utxs = UserTransactionServerImp.getSingleton();
            utxs.init(name, properties);           
        }
        
        autoRegisterResources = configProperties.getAsBoolean(AUTOMATIC_RESOURCE_REGISTRATION_PROPERTY_NAME);
//        if ( Configuration.getResources().hasMoreElements() && !autoRegisterResources ) {
//        	AcceptAllXATransactionalResource defaultRes = new AcceptAllXATransactionalResource (
//                    "com.atomikos.icatch.DefaultResource" );
//            Configuration.addResource ( defaultRes );
//
//        }
	}


	@Override
	public void afterShutdown() {
		UserTransactionServerImp.getSingleton().shutdown();
		TransactionManagerImp.installTransactionManager ( null, false );
		XaResourceRecoveryManager.installXaResourceRecoveryManager(null, null);
	}

	@Override
	public void afterInit() {
		TransactionManagerImp.installTransactionManager(Configuration.getCompositeTransactionManager(), autoRegisterResources);
		RecoveryLog recoveryLog = Configuration.getRecoveryLog();
		long maxTimeout = Configuration.getConfigProperties().getMaxTimeout();
		if (recoveryLog != null) {
			XaResourceRecoveryManager.installXaResourceRecoveryManager(new DefaultXaRecoveryLog(recoveryLog, maxTimeout),Configuration.getConfigProperties().getTmUniqueName());
		}
		
	}

}
