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
import com.atomikos.icatch.system.Configuration;
import com.atomikos.jdbc.AtomikosSQLException;
import com.atomikos.util.ClassLoadingHelper;


 /**
  * 
  * 
  * @author guy
  *
  */

class AtomikosNonXAConnectionFactory implements ConnectionFactory 
{
	
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


			Class driverClass = ClassLoadingHelper.loadClass ( driverClassName );
            
            Object aDriver = driverClass.newInstance();
            
            if ( ! ( aDriver instanceof java.sql.Driver ) ) {
            	String msg = "Driver class '" + driverClassName + "' does not seem to be a valid JDBC driver - please check the spelling and verify your JDBC vendor's documentation";
            	AtomikosSQLException.throwAtomikosSQLException ( msg );
            } 
            driver = (Driver) aDriver;
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
			Configuration.logWarning ( "NonXAConnectionFactory: failed to create connection: " , e );
			throw new CreateConnectionException ( "Could not create JDBC connection" , e );
		}
		return new AtomikosNonXAPooledConnection ( c , props , readOnly );
	}

}
