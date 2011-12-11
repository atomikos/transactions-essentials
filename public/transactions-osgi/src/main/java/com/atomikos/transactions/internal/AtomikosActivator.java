/**
 * Copyright (C) 2000-2010 Atomikos <info@atomikos.com>
 *
 * This code ("Atomikos TransactionsEssentials"), by itself,
 * is being distributed under the
 * Apache License, Version 2.0 ("License"), a copy of which may be found at
 * http://www.atomikos.com/licenses/apache-license-2.0.txt .
 * You may not use this file except in compliance with the License.
 *
 * While the License grants certain patent license rights,
 * those patent license rights only extend to the use of
 * Atomikos TransactionsEssentials by itself.
 *
 * This code (Atomikos TransactionsEssentials) contains certain interfaces
 * in package (namespace) com.atomikos.icatch
 * (including com.atomikos.icatch.Participant) which, if implemented, may
 * infringe one or more patents held by Atomikos.
 * It should be appreciated that you may NOT implement such interfaces;
 * licensing to implement these interfaces must be obtained separately from Atomikos.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package com.atomikos.transactions.internal;

import java.util.Dictionary;
import java.util.Hashtable;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.atomikos.diagnostics.Console;
import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.icatch.system.Configuration;

/**
 * @author pascalleclercq When transactions-osgi bundle starts It register theses Impl in the service registry.
 */
public class AtomikosActivator implements BundleActivator {
	private UserTransactionManager utm;
	private ServiceRegistration utmRegistration;
	private ServiceRegistration userTransactionRegistration;
	private UserTransactionImp userTransaction;

	public void start(BundleContext context) throws Exception {
		try {
			// TransactionManager
			utm = new UserTransactionManager();
			// TODO: need to check the possible values for "osgi.jndi.service.name"
			utm.init();
			Dictionary<String, String> tmProps = new Hashtable<String, String>();
			tmProps.put("osgi.jndi.service.name", "AtomikosV3");
			utmRegistration = context.registerService(TransactionManager.class.getName(), utm, tmProps);
			// UserTransaction
			userTransaction = new UserTransactionImp();
			Dictionary<String, String> utmProps = new Hashtable<String, String>();
			utmProps.put("osgi.jndi.service.name", "AtomikosV3");
			userTransactionRegistration = context.registerService(UserTransaction.class.getName(), userTransaction, utmProps);
		} catch (Exception e) {
			Configuration.getConsole().print(e.getMessage(), Console.WARN);
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
			Configuration.getConsole().print(e.getMessage(), Console.WARN);
		}

	}

}
