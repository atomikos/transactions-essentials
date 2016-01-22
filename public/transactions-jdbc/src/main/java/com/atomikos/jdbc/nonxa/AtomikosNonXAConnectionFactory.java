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

package com.atomikos.jdbc.nonxa;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import com.atomikos.datasource.pool.ConnectionFactory;
import com.atomikos.datasource.pool.ConnectionPoolProperties;
import com.atomikos.datasource.pool.CreateConnectionException;
import com.atomikos.datasource.pool.XPooledConnection;
import com.atomikos.jdbc.AtomikosSQLException;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.util.ClassLoadingHelper;


 /**
  * 
  * 
  * @author guy
  *
  */

class AtomikosNonXAConnectionFactory implements ConnectionFactory 
{
	private static final Logger LOGGER = LoggerFactory.createLogger(AtomikosNonXAConnectionFactory.class);
	
	private String url;
	private String driverClassName;
	private String user;
	private String password;
	private ConnectionPoolProperties props;
	private int loginTimeout;
	private boolean readOnly;
	
	
	private Driver driver;
	protected Properties connectionProperties = new Properties();
	public AtomikosNonXAConnectionFactory ( ConnectionPoolProperties props , 
			String url , String driverClassName , String user , 
			String password , int loginTimeout , boolean readOnly )
	{
		this.props = props;
		this.user = user;
		this.password = password;
		this.url = url;
		this.driverClassName = driverClassName;
		this.loginTimeout = loginTimeout;
		this.readOnly = readOnly;
	}
	
	public void init() throws SQLException 
	{
		try {
			Class<java.sql.Driver> driverClass = ClassLoadingHelper.loadClass ( driverClassName );
            driver = driverClass.newInstance();
            if(user!=null){
            	connectionProperties.put("user", user);	
            }
            if(password!=null){
            	connectionProperties.put("password",password);
            }
        } catch ( InstantiationException e ) {
           AtomikosSQLException.throwAtomikosSQLException ( "Could not instantiate driver class: "
                    + driverClassName );
        } catch ( IllegalAccessException e ) {
        	 AtomikosSQLException.throwAtomikosSQLException  ( e.getMessage () );
        } catch ( ClassNotFoundException e ) {
        	 AtomikosSQLException.throwAtomikosSQLException  ( "Driver class not found: '"
                    + driverClassName + "' - please make sure the spelling is correct." );
        } catch (ClassCastException cce){
        	String msg = "Driver class '" + driverClassName + "' does not seem to be a valid JDBC driver - please check the spelling and verify your JDBC vendor's documentation";
        	AtomikosSQLException.throwAtomikosSQLException ( msg );
        }
        DriverManager.setLoginTimeout ( loginTimeout );
	}
	
	private Connection getConnection() throws SQLException 
	{
		
		Connection ret = null;
	    //case : 61748 Usage of drivermanager is not possible, as it does not respect the ContextClassLoader
        //ret = DriverManager.getConnection ( url , user, password );
	    ret= driver.connect(url, connectionProperties);
        return ret;
	}

	public XPooledConnection createPooledConnection()
			throws CreateConnectionException {
		Connection c;
		try {
			c = getConnection();
		} catch (SQLException e) {
			LOGGER.logWarning ( "NonXAConnectionFactory: failed to create connection: " , e );
			throw new CreateConnectionException ( "Could not create JDBC connection" , e );
		}
		return new AtomikosNonXAPooledConnection ( c , props , readOnly );
	}

}
