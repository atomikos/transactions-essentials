package com.atomikos.icatch.jta;

import java.util.Properties;
import java.util.ServiceLoader;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.atomikos.icatch.TransactionServicePlugin;

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
	public void testSetsSerialJtaTransactions() {
		properties.setProperty("com.atomikos.icatch.serial_jta_transactions", "true");
		plugin.beforeInit(properties);
		Assert.assertTrue(TransactionManagerImp.getDefaultSerial());
		
		properties.setProperty("com.atomikos.icatch.serial_jta_transactions", "false");
		plugin.beforeInit(properties);
		Assert.assertFalse(TransactionManagerImp.getDefaultSerial());
		
		properties.setProperty("com.atomikos.icatch.serial_jta_transactions", "true");
		plugin.beforeInit(properties);
		Assert.assertTrue(TransactionManagerImp.getDefaultSerial());
	}
	
}
