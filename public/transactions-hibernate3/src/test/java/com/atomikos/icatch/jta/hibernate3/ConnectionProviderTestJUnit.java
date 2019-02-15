/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.jta.hibernate3;

import java.sql.Connection;
import java.util.Properties;

import javax.sql.XAConnection;
import javax.sql.XADataSource;

import junit.framework.TestCase;

import org.hibernate.HibernateException;
import org.mockito.Answers;
import org.mockito.Mockito;

import com.atomikos.beans.PropertyException;

public class ConnectionProviderTestJUnit extends TestCase {

	public void testConnectionProviderXaDataSource() throws Exception {
		XADataSource xaDataSource=  Mockito.mock(XADataSource.class);
		XAConnection mockedXAConnection = Mockito.mock(XAConnection.class);
		Mockito.when(xaDataSource.getXAConnection()).thenReturn(mockedXAConnection);
		Connection mockedConnection = Mockito.mock(Connection.class);
		Mockito.when(mockedXAConnection.getConnection()).thenReturn(mockedConnection);
		Mockito.when(mockedConnection.isValid(Mockito.anyInt())).thenReturn(true);
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
