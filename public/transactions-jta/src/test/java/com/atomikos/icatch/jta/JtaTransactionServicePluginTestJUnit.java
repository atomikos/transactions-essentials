/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.jta;

import java.rmi.registry.LocateRegistry;
import java.util.Properties;
import java.util.ServiceLoader;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.atomikos.icatch.TransactionServicePlugin;
import com.atomikos.icatch.config.Configuration;
import com.atomikos.icatch.imp.CompositeTransactionManagerImp;

public class JtaTransactionServicePluginTestJUnit {

	private JtaTransactionServicePlugin plugin;
	private Properties properties;
	
	@Before
	public void setUp() throws Exception {
		plugin = new JtaTransactionServicePlugin();
		properties = new Properties();
		properties.setProperty("com.atomikos.icatch.default_jta_timeout", "0");
		properties.setProperty("com.atomikos.icatch.serial_jta_transactions", "true");
		properties.setProperty("com.atomikos.icatch.tm_unique_name", "bla");
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
		Configuration.getConfigProperties().setProperty("com.atomikos.icatch.default_jta_timeout", "20000");
		plugin.beforeInit();
		Assert.assertEquals(20, TransactionManagerImp.getDefaultTimeout());
	}
	
	@Test
	public void testSetSerialJtaTransactionsFalse() {
	    Configuration.getConfigProperties().setProperty("com.atomikos.icatch.serial_jta_transactions", "false");
		plugin.beforeInit();
		Assert.assertFalse(TransactionManagerImp.getDefaultSerial());
	}
	
	@Test
	public void testSetsSerialJtaTransactionsTrue() {
	    Configuration.getConfigProperties().setProperty("com.atomikos.icatch.serial_jta_transactions", "true");
		plugin.beforeInit();
		Assert.assertTrue(TransactionManagerImp.getDefaultSerial());
	}
	
	

	
	@Test
	public void testAfterInitInstallsJtaTransactionManager() {
		Configuration.installCompositeTransactionManager(new CompositeTransactionManagerImp());
		Configuration.init();
		plugin.afterInit();
		Assert.assertNotNull(TransactionManagerImp.getTransactionManager());
		Configuration.shutdown(true);
	}
}