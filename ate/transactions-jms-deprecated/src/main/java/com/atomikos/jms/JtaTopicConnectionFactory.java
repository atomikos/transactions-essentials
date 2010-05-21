package com.atomikos.jms;

import java.util.HashMap;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.XATopicConnection;
import javax.jms.XATopicConnectionFactory;
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
 * A connection factory that couples JMS topic sends/receives to JTA
 * transactions. Use this class only if you need to do explicit resource
 * registration with the transaction service (i.e., when the underlying
 * vendor-specific XAResource implementation is not fully compliant with the
 * JTA/XA specifications, or if you want to force recovery at TM startup time).
 * In all other cases, the TopicConnectionFactoryBean class is preferred over
 * this one.
 * <p>
 * Use this class if you want to make topic send/receive operations within the
 * scope of a JTA transaction. This class requires explicit resource
 * registration with the transaction service: you need to explicitly call
 * getTransactionalResource() and register the result with the
 * UserTransactionService:
 * <p>
 * <code>
 *XATopicConnectionFactory xafactory = ... //vendor-specific code<br>
 *JtaTopicConnectionFactory factory = <br>
 *	new JtaTopicConnectionFactory ( xafactory );<br>
 *com.atomikos.datasource.TransactionalResource resource = factory.getTransactionalResource();<br>
 *com.atomikos.icatch.UserTransactionService uts = new com.atomikos.icatch.UserTransactionServiceImp();<br>
 *uts.registerResource ( resource );<br>
 *</code>
 * 
 * <p>
 * Topic functionality in this product was sponsored by <a href="http://www.webtide.com">Webtide</a>.
 */

public class JtaTopicConnectionFactory implements TopicConnectionFactory,
		Referenceable 
{
    private static HashMap nameToFactory_ = new HashMap ();
    // for JNDI lookup: maps name to instance

    private XATopicConnectionFactory factory_;
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
        Reference ref = new Reference ( JtaTopicConnectionFactory.class
                .getName (), new StringRefAddr ( "name",
                "JtaTopicConnectionFactory" ), JndiObjectFactory.class
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
     * @return JtaTopicConnectionFactory The factory, null if not there.
     */

    static JtaTopicConnectionFactory getInstance ( String name )
    {
        JtaTopicConnectionFactory ret = ( JtaTopicConnectionFactory ) nameToFactory_
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

    static synchronized JtaTopicConnectionFactory getOrCreate (
            String resourceName , XATopicConnectionFactory qFactory ,
            XidFactory xFactory )
    {
        JtaTopicConnectionFactory ret = getInstance ( resourceName );

        if ( ret == null ) {
            ret = new JtaTopicConnectionFactory ( resourceName, qFactory,
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
            JtaTopicConnectionFactory instance )
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

    public JtaTopicConnectionFactory ( String resourceName ,
            XATopicConnectionFactory factory )
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
     * @param tFactory
     *            The JMS vendor-supplied xa connection factory.
     * @param xFactory
     *            The XidFactory.
     */

    public JtaTopicConnectionFactory ( String resourceName ,
            XATopicConnectionFactory tFactory , XidFactory xFactory )
    {
        factory_ = tFactory;
        res_ = new JmsTransactionalResource ( resourceName, tFactory, xFactory );
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
     * Creates a default topic connection.
     * 
     */
	public TopicConnection createTopicConnection() throws JMSException 
	{
		XATopicConnection tc = factory_.createXATopicConnection();
		return new JtaTopicConnection ( tc , res_ );
	}

	/**
	 * Creates a topic connection for the given user and password.
	 */
	public TopicConnection createTopicConnection ( String userName , String password )
			throws JMSException 
	{
		XATopicConnection tc = factory_.createXATopicConnection ( userName , password );
		return new JtaTopicConnection ( tc , res_ );
	}

	/**
	 * Creates a default connection.
	 */
	public Connection createConnection() throws JMSException 
	{
		return createTopicConnection();
	}

	/**
	 * Creates a default connection for the given user and password.
	 */
	public Connection createConnection ( String userName , String password )
			throws JMSException {
		return createTopicConnection ( userName , password );
	}

	
	public Reference getReference() throws NamingException 
	{
		return createReference ( res_.getName() ); 
	}





}
