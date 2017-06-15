/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jms;

import java.io.Serializable;
import java.util.Properties;
import java.util.Set;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.XAConnectionFactory;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;

import com.atomikos.beans.PropertyUtils;
import com.atomikos.datasource.RecoverableResource;
import com.atomikos.datasource.pool.ConnectionFactory;
import com.atomikos.datasource.pool.ConnectionPool;
import com.atomikos.datasource.pool.ConnectionPoolException;
import com.atomikos.datasource.pool.ConnectionPoolProperties;
import com.atomikos.datasource.pool.CreateConnectionException;
import com.atomikos.datasource.pool.PoolExhaustedException;
import com.atomikos.datasource.xa.event.XAResourceDetectedEvent;
import com.atomikos.datasource.xa.jms.JmsTransactionalResource;
import com.atomikos.icatch.config.Configuration;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.publish.EventPublisher;
import com.atomikos.util.ClassLoadingHelper;
import com.atomikos.util.IntraVmObjectFactory;
import com.atomikos.util.IntraVmObjectRegistry;

 /**
  * This class represents the Atomikos JMS 1.1 connection factory for JTA-enabled JMS. 
  * Use an instance of this class to make JMS participate in JTA transactions without 
  * having to issue the low-level XA calls yourself. The following use cases are supported:
  * 
  * <br/>
  * <dl>
  * <dt><strong>JTA/XA-enabled JMS</strong></dt>
  * <dd>
  * This class can be used as a connection factory in JTA/XA use cases, in order to make the 
  * JTA transaction control the effects of JMS operations 
  * (as explained in <a href="http://www.atomikos.com/Publications/ReliableJmsWithTransactions">http://www.atomikos.com/Publications/ReliableJmsWithTransactions</a>).
  * In order to use the JTA/XA mode, make sure that the following conditions hold:
  * <ol>
  * <li>
  * The <code>localTransactionMode</code> is set to <b>false</b> (default), and
  * </li>
  * <li>
  * Sessions are created in transacted mode, i.e.: <br/>
  * <code>
  *     Transaction tx = ...; //start a JTA transaction<br/>
  *     ...
  * 	AtomikosConnectionFactoryBean cf = ...; //create or retrieve a connection factory instance<br/>
  * 	...<br/>
  * 	Connection c = cf.createConnection();<br/>
  * 	Session s = c.createSession ( true , 0 ); //note the value of the transacted flag!<br/>
  *     ...//perform regular JMS sends or receives<br/>
  *     ...<br/>
  *     tx.commit(); //commit JTA transaction to make all JMS operations take effect, or rollback to cancel everything<br/>
  * </code>
  * </li>
  * </ol>
  * The advantage of this class over using a vendor-specific <code>XAConnectionFactory</code> is that this class takes care of all JTA/XA-related
  * calls towards the driver underneath. In other words, it hides the complexities of JTA/XA at the driver level.
  * </dd>
  * 
  * <dt><strong>Local JMS transactions</strong></dt>
  * <dd>
  * This class can be used to demarcate transactions yourself, on the JMS session object (as opposed to using a JTA/XA transaction).<br/>
  * <strong>WARNING:</strong> as per JMS specification, <strong>this mode is only safe if you access no other back-end system like JDBC.</strong><br/>
  * In order to enable this mode, 
  * make sure that the following conditions hold:
  * <ol>
  * <li>
  * The <code>localTransactionMode</code> is set to <b>true</b>, and
  * </li>
  * <li>
  * Sessions are created in transacted mode, i.e.: <br/>
  * <code>
  * 	AtomikosConnectionFactoryBean cf = ...; //create or retrieve a connection factory instance<br/>
  * 	...<br/>
  * 	Connection c = cf.createConnection();<br/>
  * 	Session s = c.createSession ( true , 0 ); //note the value of the transacted flag!<br/>
  * 	...//perform regular JMS sends or receives<br/>
  * 	...<br/>
  *     s.commit();//commit on session to make all effects permanent, rollback to cancel
  * </code>
  * </li>
  * </ol>
  * Note: this mode requires support from your JMS vendor's XAConnectionFactory implementation. Check your vendor documentation first!
  * </dd>
  * 
  * <dt><strong>Non-transacted JMS</strong></dt>
  * <dd>
  * This class can be used for sending and receiving in non-transacted mode, of localTransactionMode set to true.<br/>
  * <strong>WARNING:</strong> as per JMS specification, <strong>this mode gives hardly any guarantees in terms of consistency after failures.</strong><br/>
  * To enable this mode, make sure to create the session as follows:<br/>
  * 
  * <code>
  * 	AtomikosConnectionFactoryBean cf = ...; //create or retrieve a connection factory instance<br/>
  * 	...<br/>
  * 	Connection c = cf.createConnection();<br/>
  * 	Session s = c.createSession ( false , ... ); //note the value of the transacted flag!<br/>
  * </code>
  * Note: this mode requires support from your JMS vendor's XAConnectionFactory implementation. Check your vendor documentation first!
  * </dd>
  * </dl>
  */

public class AtomikosConnectionFactoryBean 
implements javax.jms.ConnectionFactory, ConnectionPoolProperties, 
Referenceable, Serializable {
	private static final Logger LOGGER = LoggerFactory.createLogger(AtomikosConnectionFactoryBean.class);

	private static final long serialVersionUID = 1L;

	private String uniqueResourceName;
	private int maxPoolSize;
	private int minPoolSize;
	private String xaConnectionFactoryClassName; 
	private int borrowConnectionTimeout;
	private Properties xaProperties = null;
	private transient ConnectionPool connectionPool;
	private transient XAConnectionFactory xaConnectionFactory;
	private int maintenanceInterval;
	private int maxIdleTime;
	private int reapTimeout;
	private boolean localTransactionMode;
	private int maxLifetime;

	private boolean ignoreSessionTransactedFlag;
	
	public AtomikosConnectionFactoryBean()
	{
		minPoolSize = 1;
		maxPoolSize = 1;
		borrowConnectionTimeout = 30;
		maintenanceInterval = 60;
		maxIdleTime = 60;
		reapTimeout = 0;
		ignoreSessionTransactedFlag = true;
		xaProperties = new Properties();
	}
	
	private void throwAtomikosJMSException ( String msg ) throws AtomikosJMSException 
	{
		throwAtomikosJMSException ( msg , null );
	}

	private void throwAtomikosJMSException ( String msg , Throwable cause ) 
	throws AtomikosJMSException
	{
		AtomikosJMSException.throwAtomikosJMSException(msg, cause);
	}
	
	/**
	 * Gets the max size of the pool.
	 * @return int The max size of the pool.
	 */
	public int getMaxPoolSize()
	{
		return maxPoolSize;
	}
	
	/**
	 * Sets the max size that the pool can reach. Optional, defaults to 1.
	 * @param maxPoolSize
	 */
	public void setMaxPoolSize(int maxPoolSize)
	{
		this.maxPoolSize = maxPoolSize;
	}

	/**
	 * Gets the min size of the pool. 
	 * 
	 * @return The min size.
	 * 
	 */
	public int getMinPoolSize()
	{
		return minPoolSize;
	}
	
	/**
	 * Sets the min size of the pool. Optional, defaults to 1.
	 * 
	 * @param minPoolSize
	 */
	public void setMinPoolSize(int minPoolSize)
	{
		this.minPoolSize = minPoolSize;
	}

	/**
	 * Sets both the min and max size of the pool. Optional.
	 * 
	 * Overrides any minPoolSize or maxPoolSize that you might
	 * have set before!
	 * 
	 * @param minAndMaxSize
	 */
	
	public void setPoolSize ( int minAndMaxSize )
	{
		setMinPoolSize ( minAndMaxSize );
		setMaxPoolSize ( minAndMaxSize );
	}
	
	/**
	 * Gets the unique name for this resource. 
	 * 
	 * @return The name.
	 */
	public String getUniqueResourceName()
	{
		return uniqueResourceName;
	}
	
	/**
	 * Sets the unique resource name for this resource, needed for recovery of transactions after restart. Required.
	 * 
	 * @param resourceName
	 */
	
	public void setUniqueResourceName(String resourceName)
	{
		this.uniqueResourceName = resourceName;
	}

	/**
	 * Gets the name of the vendor-specific XAConnectionFactory class implementation.
	 * 
	 * @return The name of the vendor class.
	 */
	public String getXaConnectionFactoryClassName()
	{
		return xaConnectionFactoryClassName;
	}
	
	/**
	 * Sets the fully qualified name of a vendor-specific implementation of XAConnectionFatory. 
	 * Required, unless you call setXaConnectionFactory. 
	 *
	 * @param xaConnectionFactoryClassName
	 * 
	 * @see javax.jms.XAConnectionFactory
	 */
	public void setXaConnectionFactoryClassName(String xaConnectionFactoryClassName)
	{
		this.xaConnectionFactoryClassName = xaConnectionFactoryClassName;
	}

	/**
	 * Gets the vendor-specific XA properties to set. 
	 * 
	 * @return The properties as key,value pairs.
	 */
	public Properties getXaProperties()
	{
		return xaProperties;
	}
	
	/**
	 * Sets the vendor-specific XA properties. 
	 * Required, unless you call setXaConnectionFactory. 
	 * 
	 * @param xaProperties The properties, to be set (during initialization) on the
	 * specified XAConnectionFactory implementation.
	 */
	
	public void setXaProperties(Properties xaProperties)
	{
		this.xaProperties = xaProperties;
	}
	
	/**
	 * Gets the configured XAConnectionFactory. 
	 * @return The factory, or null if not yet configured.
	 */
	public XAConnectionFactory getXaConnectionFactory()
	{
		return xaConnectionFactory;
	}
	
	/**
	 * Sets the XAConnectionFactory directly, instead of calling setXaConnectionFactoryClassName and setXaProperties. 
	 * 
	 * @param xaConnectionFactory
	 */
	public void setXaConnectionFactory(XAConnectionFactory xaConnectionFactory)
	{
		this.xaConnectionFactory = xaConnectionFactory;
	}

	/**
	 * Sets the maximum amount of seconds that a connection is kept in the pool before 
	 * it is destroyed automatically. Optional, defaults to 0 (no limit).
	 * @param maxLifetime
	 */
	public void setMaxLifetime(int maxLifetime) {
		this.maxLifetime = maxLifetime;
	}
	
	/**
	 * Gets the maximum lifetime in seconds.
	 * 
	 */
	public int getMaxLifetime() {
		return maxLifetime;
	}

	
	/**
	 * Initializes the instance. It is highly recommended that this method be 
	 * called early after VM startup, to ensure that recovery can start as soon as possible.
	 * 
	 * @throws JMSException
	 */
	public synchronized void init() throws JMSException
	{
		if ( LOGGER.isDebugEnabled() ) LOGGER.logInfo ( this + ": init..." );
		if (connectionPool != null)
			return;
		
		if (maxPoolSize < 1)
			throwAtomikosJMSException("Property 'maxPoolSize' of class AtomikosConnectionFactoryBean must be greater than 0, was: " + maxPoolSize);
		if (minPoolSize < 0 || minPoolSize > maxPoolSize)
			throwAtomikosJMSException("Property 'minPoolSize' of class AtomikosConnectionFactoryBean must be at least 0 and at most maxPoolSize, was: " + minPoolSize);
		if (getUniqueResourceName() == null)
			throwAtomikosJMSException("Property 'uniqueResourceName' of class AtomikosConnectionFactoryBean cannot be null.");
		
		try {
			getReference();
			ConnectionFactory cf = doInit();
			connectionPool = new ConnectionPool(cf, this);
			
		} catch ( AtomikosJMSException e ) {
			//don't log: AtomikosJMSException is logged on creation by the factory methods
			throw e;
		}
		 catch ( Exception ex) {
			//don't log: AtomikosJMSException is logged on creation by the factory methods
			throwAtomikosJMSException("Cannot initialize AtomikosConnectionFactoryBean", ex);
		}
		if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": init done." );

	}
	
	private String printXaProperties() {
    StringBuilder ret = new StringBuilder();
		if ( xaProperties != null ) {
			Set<String> it = xaProperties.stringPropertyNames();
			ret.append ( "[" );
			boolean first = true;
			for (String name : it) {
				if ( ! first ) ret.append ( "," );
				String value = xaProperties.getProperty( name);
				ret.append ( name ); ret.append ( "=" ); ret.append ( value );
				first = false;
			}
			ret.append ( "]" );
		}
		return ret.toString();
	}
	
	private com.atomikos.datasource.pool.ConnectionFactory doInit() throws Exception 
	{
		if (xaConnectionFactory == null) {
			if (xaConnectionFactoryClassName == null)
				throwAtomikosJMSException("Property 'xaConnectionFactoryClassName' of class AtomikosConnectionFactoryBean cannot be null.");
			if (xaProperties == null)
				throwAtomikosJMSException("Property 'xaProperties' of class AtomikosConnectionFactoryBean cannot be null.");
		}
		
		
		if ( LOGGER.isDebugEnabled() ) LOGGER.logInfo(
				this + ": initializing with [" +
				" xaConnectionFactory=" + xaConnectionFactory + "," +
				" xaConnectionFactoryClassName=" + xaConnectionFactoryClassName + "," +
				" uniqueResourceName=" + getUniqueResourceName() + "," +
				" maxPoolSize=" + getMaxPoolSize() + "," +
				" minPoolSize=" + getMinPoolSize() + "," +
				" borrowConnectionTimeout=" + getBorrowConnectionTimeout() + "," +
				" maxIdleTime=" + getMaxIdleTime() + "," +
				" reapTimeout=" + getReapTimeout() + "," +
				" maintenanceInterval=" + getMaintenanceInterval() + "," +
				" xaProperties=" + printXaProperties() + "," +
				" localTransactionMode=" + localTransactionMode + "," + 
				" maxLifetime=" + maxLifetime + "," +
				" ignoreSessionTransactedFlag=" + ignoreSessionTransactedFlag +
				"]"
				);
		
		if (xaConnectionFactory == null) {
			try {
				Class<XAConnectionFactory> xaClass = ClassLoadingHelper.loadClass ( xaConnectionFactoryClassName );
				xaConnectionFactory = xaClass.newInstance();
			} catch ( ClassNotFoundException notFound ) {
				AtomikosJMSException.throwAtomikosJMSException ( "The class '" + xaConnectionFactoryClassName +
						"' specified by property 'xaConnectionFactoryClassName' of class AtomikosConnectionFactoryBean could not be found in the classpath. " +
						"Please make sure the spelling in your setup is correct, and that the required jar(s) are in the classpath." , notFound );
			} catch (ClassCastException cce) {
				AtomikosJMSException.throwAtomikosJMSException ( "The class '" + xaConnectionFactoryClassName +
						"' specified by property 'xaConnectionFactoryClassName' of class AtomikosConnectionFactoryBean does not implement the required interface javax.jms.XAConnectionFactory. " +
						"Please make sure the spelling in your setup is correct, and check your JMS driver vendor's documentation." );
			}
			
			PropertyUtils.setProperties(xaConnectionFactory, xaProperties );
		}
			
		JmsTransactionalResource tr = new JmsTransactionalResource(getUniqueResourceName() , xaConnectionFactory);
		com.atomikos.datasource.pool.ConnectionFactory cf = new com.atomikos.jms.AtomikosJmsXAConnectionFactory(xaConnectionFactory, tr, this);
		Configuration.addResource ( tr );
		EventPublisher.publish(new XAResourceDetectedEvent(xaConnectionFactoryClassName,xaProperties,XAResourceDetectedEvent.ResourceType.JMS));
		return cf;
	}
	
	
	/**
	 * Gets the timeout for borrowing connections from the pool.
	 * 
	 * @return int The timeout in seconds, during which connection requests should wait
	 * when no connection is available.
	 */
	public int getBorrowConnectionTimeout()
	{
		return borrowConnectionTimeout;
	}

	/**
	 * Gets the pool maintenance interval.
	 * @return int The interval in seconds.
	 */
	public int getMaintenanceInterval()
	{
		return maintenanceInterval;
	}

	/**
	 * Gets the max idle time for connections in the pool.
	 * 
	 * @return int The max time in seconds.
	 */
	public int getMaxIdleTime()
	{
		return maxIdleTime;
	}

	/**
	 * Gets the reap timeout of the pool.
	 * @return int The timeout in seconds.
	 */
	public int getReapTimeout()
	{
		return reapTimeout;
	}

	/**
	 * Gets a test query, currently defaults to null (not applicable to JMS).
	 */
	public String getTestQuery()
	{
		//not supported for now - maybe later?
		return null;
	}

	/**
	 * Sets the max timeout that connection requests should
	 * wait when no connection is available in the pool. Optional, defaults to 30 seconds.
	 * @param timeout The timeout in seconds.
	 */
	public void setBorrowConnectionTimeout ( int timeout )
	{
		this.borrowConnectionTimeout = timeout;
	}

	/**
	 * Sets the pool maintenance interval, i.e. the time (in seconds) between
	 * consecutive runs of the maintenance thread. Optional, defaults to 60 seconds.
	 * @param interval
	 */
	
	public void setMaintenanceInterval ( int interval )
	{
		this.maintenanceInterval = interval;
	}
	
	/**
	 * Sets the max idle time after which connections are cleaned up 
	 * from the pool. Optional, defaults to 60 seconds.
	 * 
	 * @param time
	 */

	public void setMaxIdleTime ( int time )
	{
		this.maxIdleTime = time;
	}

	/**
	 * Sets the reap timeout for connections. Borrowed connections that 
	 * are not closed before this timeout will be closed by the pool
	 * and returned. Optional, defaults to 0 (no timeout). 
	 * 
	 * @param timeout
	 */
	public void setReapTimeout ( int timeout ) 
	{
		this.reapTimeout = timeout;
	}

	/**
	 * Gets the local transaction mode.
	 * 
	 * @return boolean If true, then transactions are not done in XA mode but in local mode.
	 */
	public boolean getLocalTransactionMode() {
		return localTransactionMode;
	}

	/**
	 * Sets whether or not local transactions are desired. With local transactions,
	 * no XA enlist will be done - rather, the application should perform session-level
	 * JMS commit or rollback, or use explicit acknowledgement modes. 
	 * Note that this feature also requires support from
	 * your JMS vendor. Optional, defaults to false. 
	 * 
	 * @param mode
	 */
	public void setLocalTransactionMode ( boolean mode ) {
		this.localTransactionMode = mode;
	}
	
	/* JMS does not support isolation levels */
	public int getDefaultIsolationLevel() {
		return -1;
	}
	


	/**
	 * Closes the instance. This method should be called when you are done using the factory.
	 */
	public synchronized void close() 
	{
		if ( LOGGER.isDebugEnabled() ) LOGGER.logInfo ( this + ": close..." );
		if ( connectionPool != null ) {
			connectionPool.destroy();
			connectionPool = null;
		}
		
		try {
			IntraVmObjectRegistry.removeResource ( getUniqueResourceName() );
		} catch ( NameNotFoundException e ) {
			//ignore but log
			if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": error removing from JNDI" , e );
		}
		
		RecoverableResource res = Configuration.getResource ( getUniqueResourceName() );
		if ( res != null ) {
			Configuration.removeResource ( getUniqueResourceName() );
			//fix for case 26005: close resource!
			res.close();
		}
		
		if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": close done." );
	}
	
	public String toString() 
	{
		String ret = "AtomikosConnectionFactoryBean";
		String name = getUniqueResourceName();
		if ( name != null ) {
			ret = ret + " '" + name + "'";
		}
		return ret;
	}
	

	/**
	 * @see javax.jms.ConnectionFactory
	 */
	public javax.jms.Connection createConnection() throws JMSException
	{
		if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": createConnection()..." );
		Connection ret = null;
		try {
			init();
			ret =  (Connection) connectionPool.borrowConnection();
		} catch (CreateConnectionException ex) {
			throwAtomikosJMSException("Failed to grow the connection pool", ex);
		} catch (PoolExhaustedException e) {
			throwAtomikosJMSException ( "Connection pool exhausted - try increasing 'maxPoolSize' and/or 'borrowConnectionTimeout' on the AtomikosConnectionFactoryBean." , e );
		} catch (ConnectionPoolException e) {
			throwAtomikosJMSException ( "Error borrowing connection", e );
		}
		if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": createConnection() returning " + ret );
		return ret;
	}

	/**
	 * @see javax.jms.ConnectionFactory
	 */
	
	public javax.jms.Connection createConnection ( String user, String password ) throws JMSException
	{
		LOGGER.logWarning ( this + ": createConnection ( user , password ) ignores authentication - returning default connection" );
		return createConnection();
	}


	/**
	 * @see javax.naming.Referenceable
	 */
	public Reference getReference() throws NamingException
	{
		Reference ret = null;
		if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": getReference()..." );
		ret = IntraVmObjectFactory.createReference ( this , getUniqueResourceName() );
		if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": getReference() returning " + ret );
		return ret;
	}
	
	public int poolAvailableSize() {
		return connectionPool.availableSize();
	}

	public int poolTotalSize() {
		return connectionPool.totalSize();
	}

	public void refreshPool() {
		if (connectionPool != null) connectionPool.refresh();
	}

	public boolean getIgnoreSessionTransactedFlag() {
		return ignoreSessionTransactedFlag;
	}

	/**
	 * Sets whether or not to ignore the sessionTransacted flag 
	 * when creating JMS Sessions. If set, then by default all
	 * JMS Session objects will be XA-capable and enlist in the
	 * active JTA transaction. If not set, then you will only get
	 * XA-capable JMS Session objects if the sessionTransacted flag
	 * is set upon session creation. Set this to false to get the
	 * pre-3.9 behavior of Atomikos.
	 * 
	 * This setting is ignored for localTransactionMode: 
	 * in localTransactionMode you never get XA-capable Session objects.
	 *
	 * 
	 * @param value
	 */
	public void setIgnoreSessionTransactedFlag(boolean value) {
		this.ignoreSessionTransactedFlag = value;
	}

	
}
