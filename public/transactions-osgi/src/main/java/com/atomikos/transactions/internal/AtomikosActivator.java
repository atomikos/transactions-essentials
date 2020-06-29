/**
 * Copyright (C) 2000-2020 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.transactions.internal;

import java.util.Dictionary;
import java.util.Hashtable;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

/**
 * @author pascalleclercq When transactions-osgi bundle starts It register theses Impl in the service registry.
 */
public class AtomikosActivator implements BundleActivator {
	private static final Logger LOGGER = LoggerFactory.createLogger(AtomikosActivator.class);

	private UserTransactionManager utm;
	private ServiceRegistration utmRegistration;
	private ServiceRegistration userTransactionRegistration;
	private UserTransactionImp userTransaction;

	public void start(BundleContext context) throws Exception {
		try {
			// TransactionManager
			utm = new UserTransactionManager();
			utm.init();
			Dictionary<String, String> tmProps = new Hashtable<String, String>();
			tmProps.put("osgi.jndi.service.name", "AtomikosV5");
			utmRegistration = context.registerService(TransactionManager.class.getName(), utm, tmProps);
			// UserTransaction
			userTransaction = new UserTransactionImp();
			Dictionary<String, String> utmProps = new Hashtable<String, String>();
			utmProps.put("osgi.jndi.service.name", "AtomikosV5");
			userTransactionRegistration = context.registerService(UserTransaction.class.getName(), userTransaction, utmProps);
		} catch (Exception e) {
			LOGGER.logFatal(e.getMessage(), e);
		}
	}

	public void stop(BundleContext context) throws Exception {
		try {
			if (utmRegistration != null) {
				utmRegistration.unregister();
				utmRegistration = null;
			}

			if (utm != null) {
				utm.close();
			}
			if (userTransactionRegistration != null) {
				userTransactionRegistration.unregister();
				userTransactionRegistration = null;
			}

		} catch (Exception e) {
			LOGGER.logError(e.getMessage(), e);
		}

	}

}
