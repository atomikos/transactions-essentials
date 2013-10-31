package com.atomikos.icatch.jta;

import java.util.Properties;

import com.atomikos.icatch.TransactionServicePlugin;

public class JtaTransactionServicePlugin implements TransactionServicePlugin {
	
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


	@Override
	public void beforeInit(Properties properties) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterShutdown() {
		// TODO Auto-generated method stub
		
	}

}
