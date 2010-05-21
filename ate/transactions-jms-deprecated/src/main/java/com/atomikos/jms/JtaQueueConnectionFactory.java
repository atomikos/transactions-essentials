package com.atomikos.jms;

import java.util.HashMap;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.XAQueueConnection;
import javax.jms.XAQueueConnectionFactory;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;

import com.atomikos.datasource.xa.XidFactory;
import com.atomikos.datasource.xa.jms.JmsTransactionalResource;

/**
 * 
 * 
 * A queue connection factory that couples JMS queue sends/receives to JTA
 * transactions. Use this class only if you need to do explicit resource
 * registration with the transaction service (i.e., when the underlying
 * vendor-specific XAResource implementation is not fully compliant with the
 * JTA/XA specifications, or if you want to force recovery at TM startup time).
 * In all other cases, the QueueConnectionFactoryBean class is preferred over
 * this one.
 * <p>
 * Use this class if you want to make queue send/receive operations within the
 * scope of a JTA transaction. This class requires explicit resource
 * registration with the transaction service: you need to explicitly call
 * getTransactionalResource() and register the result with the
 * UserTransactionService:
 * <p>
 * <code>
 *XAQueueConnectionFactory xafactory = ... //vendor-specific code<br>
 *JtaQueueConnectionFactory factory = <br>
 *	new JtaQueueConnectionFactory ( xafactory );<br>
 *com.atomikos.datasource.TransactionalResource resource = factory.getTransactionalResource();<br>
 *com.atomikos.icatch.UserTransactionService uts = new com.atomikos.icatch.UserTransactionServiceImp();<br>
 *uts.registerResource ( resource );<br>
 *</code>
 * 
 * 
 */

public class JtaQueueConnectionFactory implements QueueConnectionFactory,
        Referenceable
{

    private static HashMap nameToFactory_ = new HashMap ();
    // for JNDI lookup: maps name to instance

    private XAQueueConnectionFactory factory_;
    // the wrapped xa factory

    private JmsTransactionalResource res_;

    /**
     * Create a reference for the given resource.
     * 
     * @param uniqueName
     * @return
     */
    static Reference createReference ( String uniqueName )
    {
        RefAddr ra = new StringRefAddr ( "ResourceName", uniqueName );
        Reference ref = new Reference ( JtaQueueConnectionFactory.class
                .getName (), new StringRefAddr ( "name",
                "JtaQueueConnectionFactory" ), JndiObjectFactory.class
                .getName (), null );
        ref.add ( ra );
        return ref;
    }

    /**
     * Helper method to get an instance with given name. JNDI Object Factories
     * can use this to retrieve an instance that was previously bound.
     * 
     * @param name
     *            The name of the connection factory.
     * @return JtaQueueConnectionFactory The factory, null if not there.
     */

    static JtaQueueConnectionFactory getInstance ( String name )
    {
        JtaQueueConnectionFactory ret = (JtaQueueConnectionFactory) nameToFactory_
                .get ( name );
        if ( ret != null && ret.getTransactionalResource ().isClosed () ) {
            // happens on REstart of TS
            removeFromMap ( name );
            ret = null;
        }
        return ret;
    }

    /**
     * Get or create a new instance. Utility creation method to avoid double
     * create by concurrent clients via the Bean mechanism.
     */

    static synchronized JtaQueueConnectionFactory getOrCreate (
            String resourceName , XAQueueConnectionFactory qFactory ,
            XidFactory xFactory )
    {
        JtaQueueConnectionFactory ret = getInstance ( resourceName );

        if ( ret == null ) {
            ret = new JtaQueueConnectionFactory ( resourceName, qFactory,
                    xFactory );
            addToMap ( resourceName, ret );
        }

        return ret;
    }

    /**
     * Add an instance to the map, so that it can be found by name.
     * 
     * @param name
     *            The name to map on.
     * @param instance
     *            The factory.
     */

    private synchronized static void addToMap ( String name ,
            JtaQueueConnectionFactory instance )
    {
        nameToFactory_.put ( name, instance );
        // Configuration.addResource ( instance.getTransactionalResource() );
    }

    /**
     * Remove a map entry for the given name.
     * 
     * @param name
     *            The name to unmap.
     */

    private synchronized static void removeFromMap ( String name )
    {
        nameToFactory_.remove ( name );
        // Configuration.removeResource ( name );
    }

    /**
     * Create a new instance with a given JMS vendor-supplied xa connection
     * factory.
     * 
     * @param resourceName
     *            The unique name for the transactional resource that will be
     *            created.
     * @param factory
     *            The JMS vendor-supplied xa connection factory.
     */

    public JtaQueueConnectionFactory ( String resourceName ,
            XAQueueConnectionFactory factory )
    {
        factory_ = factory;
        res_ = new JmsTransactionalResource ( resourceName, factory );

        addToMap ( res_.getName (), this );
    }

    /**
     * Create a new instance with a given JMS vendor-supplied xa connection
     * factory, and a specific XidFactory.
     * 
     * @param resourceName
     *            The unique name for the transactional resource that will be
     *            created.
     * @param qFactory
     *            The JMS vendor-supplied xa connection factory.
     * @param xFactory
     *            The XidFactory.
     */

    public JtaQueueConnectionFactory ( String resourceName ,
            XAQueueConnectionFactory qFactory , XidFactory xFactory )
    {
        factory_ = qFactory;
        res_ = new JmsTransactionalResource ( resourceName, qFactory, xFactory );
        addToMap ( res_.getName (), this );
    }

    /**
     * Gets the transactional resource created during initialization.
     * 
     * @return JmsTransactionalResource The resource. This should be added to
     *         the transaction service's recoverable resources.
     */

    public JmsTransactionalResource getTransactionalResource ()
    {
        return res_;
    }

    /**
     * Creates a default connection.
     * 
     * @return QueueConnection The connection.
     */

    public QueueConnection createQueueConnection () throws JMSException
    {
        XAQueueConnection xac = factory_.createXAQueueConnection ();
        return new JtaQueueConnection ( xac, res_ );
    }

    /**
     * Creates a connection for a given user and password.
     * 
     * @return QueueConnection The connection.
     * @param user
     *            The user name.
     * @param pw
     *            The password.
     */

    public QueueConnection createQueueConnection ( String user , String pw )
            throws JMSException
    {
        XAQueueConnection xac = factory_.createXAQueueConnection ( user, pw );
        return new JtaQueueConnection ( xac, res_ );
    }

    /**
     * @see Referenceable
     */
    public Reference getReference () throws NamingException
    {
        return createReference ( res_.getName () );
    }

    /**
     * Creates a default connection.
     * 
     * @return Connection The connection.
     */
    
	public Connection createConnection() throws JMSException 
	{
		return createQueueConnection();
	}

	/**
     * Creates a connection for a given user and password.
     * 
     * @return Connection The connection.
     * @param userName
     *            The user name.
     * @param password
     *            The password.
     */
	public Connection createConnection ( String userName , String password ) throws JMSException 
	{
		return createQueueConnection ( userName , password );
	}

}
