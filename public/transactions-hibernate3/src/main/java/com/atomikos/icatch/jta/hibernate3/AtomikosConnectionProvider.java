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

package com.atomikos.icatch.jta.hibernate3;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.connection.ConnectionProvider;

import com.atomikos.beans.PropertyException;
import com.atomikos.beans.PropertyUtils;
import com.atomikos.jdbc.AbstractDataSourceBean;
import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.atomikos.jdbc.AtomikosSQLException;
import com.atomikos.jdbc.nonxa.AtomikosNonXADataSourceBean;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

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
	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = LoggerFactory.createLogger(AtomikosConnectionProvider.class);


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
		LOGGER.logInfo("configuring AtomikosConnectionProvider with properties: " + atomikosProperties);

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
			Object value =  entry.getValue();

			if (key.startsWith(PROPERTIES_PREFIX) && !key.equals(PROPERTY_NONXA)) {
				atomikosProperties.put(key.substring(PROPERTIES_PREFIX.length()), value);
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
