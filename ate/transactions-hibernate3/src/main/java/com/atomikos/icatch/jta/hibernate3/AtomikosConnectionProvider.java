package com.atomikos.icatch.jta.hibernate3;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.connection.ConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atomikos.beans.PropertyException;
import com.atomikos.beans.PropertyUtils;
import com.atomikos.jdbc.AbstractDataSourceBean;
import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.atomikos.jdbc.AtomikosSQLException;
import com.atomikos.jdbc.nonxa.AtomikosNonXADataSourceBean;

/**
 * Atomikos-specific ConnectionProvider implementation that can
 * create Atomikos connection pools directly from the Hibernate
 * configuration.
 *
 * <p>To use the AtomikosConnectionProvider specify this class as the
 * value of the <b>hibernate.connection.provider_class</b> of the
 * hibernate configuration properties.<br/>
 * You then have to configure the {@link AtomikosDataSourceBean} properties
 * by prefixing them with <b>hibernate.connection.atomikos</b>.<br/>
 * Eg: <b>hibernate.connection.atomikos.uniqueResourceName</b><br/>
 * <b>hibernate.connection.atomikos.xaDataSourceClassName</b><br/>
 * <b>hibernate.connection.atomikos.xaProperties.databaseName</b><br/>
 * ...<br/><br/>
 * Add a <b>hibernate.connection.atomikos.nonxa=true</b> property if you want
 * to configure a {@link AtomikosNonXADataSourceBean} instead.
 * </p>
 * 
 * <p>
 * NOTE: if you use the Hibernate XML config mechanism, 
 * then the prefix should be <b>connection.atomikos</b> instead (without hibernate prefix).
 * </p>
 * 
 * @author Ludovic Orban
 */
public class AtomikosConnectionProvider implements ConnectionProvider {

	private static final Logger log = LoggerFactory.getLogger( AtomikosConnectionProvider.class );

	private static final String PROPERTIES_PREFIX = "hibernate.connection.atomikos.";
	private static final String PROPERTY_NONXA = "hibernate.connection.atomikos.nonxa";
	
	
	private AbstractDataSourceBean dataSource = null;

	
	public void close() throws HibernateException {
		if (dataSource != null)
			dataSource.close();
		dataSource = null;
	}

	public void closeConnection(Connection connection) throws SQLException {
		connection.close();
	}

	public void configure(Properties props) throws HibernateException {
		if (dataSource != null) {
			return;
		}
		
		if ("true".equalsIgnoreCase(props.getProperty(PROPERTY_NONXA))) {
			dataSource = new AtomikosNonXADataSourceBean();
		}
		else {
			dataSource = new AtomikosDataSourceBean();
		}
		
		Properties atomikosProperties = filterOutHibernateProperties(props);
		log.info("configuring AtomikosConnectionProvider with properties: " + atomikosProperties);
		
		try {
			PropertyUtils.setProperties(dataSource, atomikosProperties);
		} catch (PropertyException ex) {
			throw new HibernateException("cannot create Atomikos DataSource", ex);
		}
		
		try {
			dataSource.init();
		} catch (AtomikosSQLException ex) {
			throw new HibernateException("cannot initialize Atomikos DataSource", ex);
		}
	}

	private Properties filterOutHibernateProperties(Properties props) {
		Properties atomikosProperties = new Properties();
		
		Iterator it = props.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String key = (String) entry.getKey();
			String value = (String) entry.getValue();
			
			if (key.startsWith(PROPERTIES_PREFIX) && !key.equals(PROPERTY_NONXA)) {
				atomikosProperties.setProperty(key.substring(PROPERTIES_PREFIX.length()), value);
			}
		}
		return atomikosProperties;
	}

	public Connection getConnection() throws SQLException {
		if (dataSource == null)
			throw new HibernateException("datasource is not configured");
		return dataSource.getConnection();
	}

	public boolean supportsAggressiveRelease() {
		return true;
	}

}
