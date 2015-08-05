package com.atomikos.icatch.jta;

import java.rmi.registry.LocateRegistry;
import java.util.Properties;
import java.util.ServiceLoader;

import javax.naming.Context;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.atomikos.icatch.config.Configuration;
import com.atomikos.icatch.imp.CompositeTransactionManagerImp;
import com.atomikos.icatch.provider.TransactionServicePlugin;

public class JtaTransactionServicePluginTestJUnit {

	private JtaTransactionServicePlugin plugin;
	private Properties properties;
	
	@Before
	public void setUp() throws Exception {
		plugin = new JtaTransactionServicePlugin();
		properties = new Properties();
		properties.setProperty("com.atomikos.icatch.default_jta_timeout", "0");
		properties.setProperty("com.atomikos.icatch.serial_jta_transactions", "true");
		properties.setProperty("com.atomikos.icatch.client_demarcation", "true");
		properties.setProperty("com.atomikos.icatch.tm_unique_name", "bla");
		properties.setProperty("com.atomikos.icatch.rmi_export_class", "UnicastRemoteObject");
        properties.setProperty ( Context.INITIAL_CONTEXT_FACTORY,
                "com.sun.jndi.rmi.registry.RegistryContextFactory" );
        properties.setProperty ( Context.PROVIDER_URL, "rmi://localhost:1099" );
        properties.setProperty("com.atomikos.icatch.automatic_resource_registration", "true");
        try {
        	LocateRegistry.createRegistry ( 1099 );
        } catch (Exception ok){}
	}
	
	@After
	public void tearDown() throws Exception {
		plugin.afterShutdown();
	}

	@Test
	public void testCanBeLoadedViaServiceLoader() {
		ServiceLoader<TransactionServicePlugin> loader = ServiceLoader.load(TransactionServicePlugin.class);
		boolean found = false;
		for(TransactionServicePlugin p : loader) {
			if (p instanceof JtaTransactionServicePlugin) found = true;
		}
		Assert.assertTrue(found);
	}
	
	@Test
	public void testSetsDefaultJtaTimeout() {
		properties.setProperty("com.atomikos.icatch.default_jta_timeout", "20000");
		plugin.beforeInit(properties);
		Assert.assertEquals(20, TransactionManagerImp.getDefaultTimeout());
	}
	
	@Test
	public void testSetSerialJtaTransactionsFalse() {
		properties.setProperty("com.atomikos.icatch.serial_jta_transactions", "false");
		plugin.beforeInit(properties);
		Assert.assertFalse(TransactionManagerImp.getDefaultSerial());
	}
	
	@Test
	public void testSetsSerialJtaTransactionsTrue() {
		properties.setProperty("com.atomikos.icatch.serial_jta_transactions", "true");
		plugin.beforeInit(properties);
		Assert.assertTrue(TransactionManagerImp.getDefaultSerial());
	}
	
	@Test
	public void testClientDemarcationTrue() {
		properties.setProperty("com.atomikos.icatch.client_demarcation", "true");
		plugin.beforeInit(properties);
		Assert.assertTrue(UserTransactionServerImp.getSingleton().getUserTransaction() instanceof RemoteClientUserTransaction);
		plugin.afterShutdown();
		Assert.assertNull(UserTransactionServerImp.getSingleton().getUserTransaction());
	}

	@Test
	public void testClientDemarcationFalse() {
		properties.setProperty("com.atomikos.icatch.client_demarcation", "false");
		plugin.beforeInit(properties);
		Assert.assertFalse(UserTransactionServerImp.getSingleton().getUserTransaction() instanceof RemoteClientUserTransaction);
	}
	

	
	@Test
	public void testAfterInitInstallsJtaTransactionManager() {
		Configuration.installCompositeTransactionManager(new CompositeTransactionManagerImp());
		plugin.afterInit();
		Assert.assertNotNull(TransactionManagerImp.getTransactionManager());
	}
}