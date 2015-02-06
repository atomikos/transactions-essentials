package com.atomikos.icatch.jta.hibernate3;

import java.sql.Connection;
import java.util.Properties;

import junit.framework.TestCase;

import org.hibernate.HibernateException;

import com.atomikos.beans.PropertyException;
import com.atomikos.icatch.jta.hibernate3.mock.TestXADataSource;

public class ConnectionProviderTestJUnit extends TestCase {

	public void testConnectionProviderXaDataSource() throws Exception {
		Properties props = new Properties();
		props.setProperty("hibernate.connection.atomikos.uniqueResourceName", "aaa");
		props.setProperty("hibernate.connection.atomikos.xaDataSourceClassName", TestXADataSource.class.getName());
		//assert that XADataSource-specific properties can be set!
		//cf case 30961
		props.setProperty("hibernate.connection.atomikos.xaProperties.lastUser" , "scott" );
		
		AtomikosConnectionProvider provider = new AtomikosConnectionProvider();
		provider.configure(props);
		
		Connection conn = provider.getConnection();
		assertNotNull(conn);
		
		conn.close();
	}
	
	public void testConnectionProviderNonXaDataSource() throws Exception {
		Properties props = new Properties();
		props.setProperty("hibernate.connection.atomikos.nonxa", "true");
		props.setProperty("hibernate.connection.atomikos.uniqueResourceName", "aaa");
		props.setProperty("hibernate.connection.atomikos.xaDataSourceClassName", TestXADataSource.class.getName());
		
		AtomikosConnectionProvider provider = new AtomikosConnectionProvider();
		try {
			provider.configure(props);
			fail("non-xa datasource creation should have failed");
		} catch (HibernateException ex) {
			PropertyException pex = (PropertyException) ex.getCause();
			assertEquals("no writeable property 'xaDataSourceClassName' in class 'com.atomikos.jdbc.nonxa.AtomikosNonXADataSourceBean'", pex.getMessage());
		}
	}
	
}
