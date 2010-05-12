package com.atomikos.icatch.trmi;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import com.atomikos.icatch.config.UserTransactionService;

import junit.framework.TestCase;

public class DefaultPropertiesTestJUnit extends TestCase {

	UserTransactionService uts;
	
	protected void setUp() throws Exception {
		super.setUp();
		uts =
            new com.atomikos.icatch.trmi.UserTransactionServiceFactory().
                getUserTransactionService ( new Properties() );
	}

	protected void tearDown() throws Exception {
		uts.shutdown(true);
		super.tearDown();
	}
	
	protected String getPropertyValue ( String name ) {
		return uts.createTSInitInfo().getProperty(name);
	}
	
	protected String getHostAddress() throws UnknownHostException {
		return InetAddress.getLocalHost().getHostAddress();
	}
	
	private void assertPropertyEquals ( String name , String value ) {
		assertEquals ( value , getPropertyValue(name) );
	}

	public void testSoapCommitProtocols() {
		//by default we use all commit protocols - can be narrowed by specifying specific values
		assertEquals ( "atomikos,wsat" , getPropertyValue (UserTransactionServiceFactory.SOAP_COMMIT_PROTOCOLS_PROPERTY_NAME ));
	}
	
	public void testSoapHostAddress() throws UnknownHostException {
		//default is localhost
		assertEquals ( getHostAddress() , getPropertyValue ( UserTransactionServiceFactory.SOAP_HOST_ADDRESS_PROPERTY_NAME ));
	}
	
	public void testSoapPort() {
		//default is 8088
		assertEquals ( "8088" , getPropertyValue ( UserTransactionServiceFactory.SOAP_PORT_PROPERTY_NAME) );
	}
	
	public void testLocalEndpointsPort() {
		//null means same as SOAP port
		assertNull ( getPropertyValue ( UserTransactionServiceFactory.LOCAL_ENDPOINTS_PORT_PROPERTY_NAME ));
	}
	
	public void testConsoleFileName() {	
		assertEquals ( "tm.out" , getPropertyValue ( UserTransactionServiceFactory.CONSOLE_FILE_NAME_PROPERTY_NAME));
	}
	
	public void testConsoleLogLevel() {
		assertPropertyEquals ( UserTransactionServiceFactory.CONSOLE_LOG_LEVEL_PROPERTY_NAME , "WARN" );
	}
	
	public void testConsoleFileLimit() {
		assertPropertyEquals ( UserTransactionServiceFactory.CONSOLE_FILE_LIMIT_PROPERTY_NAME , "-1");
	}
	
	public void testConsoleFileCount() {
		assertPropertyEquals ( UserTransactionServiceFactory.CONSOLE_FILE_COUNT_PROPERTY_NAME , "1" );
	}
	
	public void testCheckpointInterval() {
		assertPropertyEquals ( UserTransactionServiceFactory.CHECKPOINT_INTERVAL_PROPERTY_NAME , "500");
	}
	
	public void testOutputDir() {
		assertPropertyEquals ( UserTransactionServiceFactory.OUTPUT_DIR_PROPERTY_NAME , "./");
	}
	
	public void testLogBaseDir() {
		assertPropertyEquals ( UserTransactionServiceFactory.LOG_BASE_DIR_PROPERTY_NAME , "./");
	}
	
	public void testLogBaseName() {
		assertPropertyEquals ( UserTransactionServiceFactory.LOG_BASE_NAME_PROPERTY_NAME , "tmlog");
	}
	
	public void testMaxActives() {
		assertPropertyEquals ( UserTransactionServiceFactory.MAX_ACTIVES_PROPERTY_NAME , "50");
	}
	
	public void testMaxTimeout() {
		assertPropertyEquals ( UserTransactionServiceFactory.MAX_TIMEOUT_PROPERTY_NAME , "60000");
	}
	
	public void testDefaultJtaTimeout() {
		assertPropertyEquals ( UserTransactionServiceFactory.DEFAULT_JTA_TIMEOUT_PROPERTY_NAME , "10000");
	}
	
	public void testSerialJtaTransactions() {
		assertPropertyEquals ( UserTransactionServiceFactory.SERIAL_JTA_TRANSACTIONS_PROPERTY_NAME, "true");

	}
	
	public void testAutomaticResourceRegistration() {
		assertPropertyEquals ( UserTransactionServiceFactory.AUTOMATIC_RESOURCE_REGISTRATION_PROPERTY_NAME , "true");
	}
	
	public void testEnableLogging() {
		assertPropertyEquals ( UserTransactionServiceFactory.ENABLE_LOGGING_PROPERTY_NAME , "true");
	}
	
	public void testTrustClientTm() {
		assertPropertyEquals ( UserTransactionServiceFactory.TRUST_CLIENT_TM_PROPERTY_NAME , "false");

	}
	
	public void testClientDemarcation() {
		assertPropertyEquals ( UserTransactionServiceFactory.TRUST_CLIENT_TM_PROPERTY_NAME , "false");

	}
	
	public void testThreaded2PC() {
		assertPropertyEquals ( UserTransactionServiceFactory.THREADED_2PC_PROPERTY_NAME , "true");

	}
	
	public void testForceShutdownOnVmExit() {
		assertPropertyEquals ( UserTransactionServiceFactory.REGISTER_SHUTDOWN_HOOK_PROPERTY_NAME , "false");
	}
	
}

