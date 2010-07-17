/*
 * Copyright 2009 Toni Menzel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.atomikos.transactions.osgi;

import static junit.framework.Assert.assertNotNull;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.bundle;
import static org.ops4j.pax.exam.CoreOptions.options;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Pascal Leclercq
 * 
 */
@RunWith(JUnit4TestRunner.class)
public class ServiceRegistryTestJUnit {

	@Inject
	BundleContext bundleContext = null;

	
	@Configuration
	public static Option[] configure() {
		return options(

		
				bundle("http://www.springsource.com/repository/app/bundle/version/download?name=com.springsource.javax.transaction&version=1.1.0&type=binary"),
//						mavenBundle().groupId("javax.transaction").artifactId(
//						"com.springsource.javax.transaction").version("1.1.0"),

				// wrappedBundle(
				// mavenBundle().groupId( "org.apache.geronimo.specs"
				// ).artifactId( "geronimo-jta_1.0.1B_spec" ).version( "1.0" )
				// ),
				mavenBundle().groupId("com.atomikos").artifactId(
						"transactions-osgi").version("3.7.0-SNAPSHOT")

		);
	}

	
	@Test
	public void transactionManagerInTheRegistry() throws InterruptedException {

		ServiceTracker tracker = new ServiceTracker(bundleContext,
				javax.transaction.TransactionManager.class.getName(), null);
		tracker.open();

	

		TransactionManager transactionManager = (TransactionManager) tracker.waitForService(5000);

		tracker.close();
		assertNotNull(transactionManager);
	}

	@Test
	public void userTransactionInTheRegistry() throws InterruptedException {

		ServiceTracker tracker = new ServiceTracker(bundleContext,
				javax.transaction.UserTransaction.class.getName(), null);
		tracker.open();
		UserTransaction userTransaction = (UserTransaction) tracker.waitForService(5000);

		tracker.close();
		assertNotNull(userTransaction);
		
	}
}
