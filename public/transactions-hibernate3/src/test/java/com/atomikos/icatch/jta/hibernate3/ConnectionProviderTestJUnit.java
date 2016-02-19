/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
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

package com.atomikos.icatch.jta.hibernate3;

import java.sql.Connection;
import java.util.Properties;

import javax.sql.XADataSource;

import junit.framework.TestCase;

import org.hibernate.HibernateException;
import org.mockito.Answers;
import org.mockito.Mockito;

import com.atomikos.beans.PropertyException;

public class ConnectionProviderTestJUnit extends TestCase {

	public void testConnectionProviderXaDataSource() throws Exception {
		XADataSource xaDataSource=  Mockito.mock(XADataSource.class,Answers.RETURNS_MOCKS.get());
		Properties props = new Properties();
		props.setProperty("hibernate.connection.atomikos.uniqueResourceName", "aaa");
		//assert that XADataSource-specific properties can be set!
		//cf case 30961
		props.setProperty("hibernate.connection.atomikos.xaProperties.lastUser" , "scott" );
		//normally, hibernate.properties file would contain hibernate.connection.atomikos.xaDataSourceClassName
		props.put("hibernate.connection.atomikos.xaDataSource", xaDataSource);
		
		AtomikosConnectionProvider provider = new AtomikosConnectionProvider();
		provider.configure(props);
		
		Connection conn = provider.getConnection();
		assertNotNull(conn);
		
		conn.close();
	}
	
	public void testConnectionProviderNonXaDataSource() throws Exception {
		XADataSource xaDataSource=  Mockito.mock(XADataSource.class,Answers.RETURNS_MOCKS.get());
		Properties props = new Properties();
		props.setProperty("hibernate.connection.atomikos.nonxa", "true");
		props.setProperty("hibernate.connection.atomikos.uniqueResourceName", "aaa");
		//normally, hibernate.properties file would contain hibernate.connection.atomikos.xaDataSourceClassName
		props.put("hibernate.connection.atomikos.xaDataSource", xaDataSource);  

		AtomikosConnectionProvider provider = new AtomikosConnectionProvider();
		try {
			provider.configure(props);
			fail("non-xa datasource creation should have failed");
		} catch (HibernateException ex) {
			PropertyException pex = (PropertyException) ex.getCause();
			assertEquals("no writeable property 'xaDataSource' in class 'com.atomikos.jdbc.nonxa.AtomikosNonXADataSourceBean'", pex.getMessage());
		}
	}
	
}
