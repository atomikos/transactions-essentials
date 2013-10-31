package com.atomikos.icatch.jta;

import java.util.ServiceLoader;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.atomikos.icatch.TransactionServicePlugin;

public class JtaTransactionServicePluginTestJUnit {

	private JtaTransactionServicePlugin plugin;
	
	@Before
	public void setUp() throws Exception {
		plugin = new JtaTransactionServicePlugin();
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

}
