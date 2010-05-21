package com.atomikos.jms;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.XAQueueConnectionFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.atomikos.datasource.TransactionalResource;
import com.atomikos.datasource.xa.DefaultXidFactory;
import com.atomikos.datasource.xa.XidFactory;
import com.atomikos.icatch.system.Configuration;

/**
 * 
 * 
 * Use this class to access JMS queues within your JTA transactions: rollback of
 * the transaction will also cancel any messages sent or received. Instances of
 * this class need a JMS vendor-specific instance of XAQueueConnectionFactory to
 * work with. Check your JMS-vendor's documentation on how to do that. Instances
 * can be set up in a GUI wizard tool and saved on disk or in JNDI. No explicit
 * registration with the transaction engine is necessary: this class does
 * everything automatically. As soon as an instance is created, it is fully
 * capable of interacting with the Atomikos transaction manager, and will
 * transparently take part in active transactions.
 * <p>
 * <b>Note: for transactional message receival, we highly recommend that you use
 * the Atomikos QueueReceiverSession class. This is because the JMS
 * specification is rather ambiguous with respect to receiving messages in a
 * (JTA) transaction. Many J2EE application servers solve this by offering
 * message-driven bean support, but our QueueReceiverSession allows you to do
 * the same with 'ordinary' MessageListener implementations. </b>
 * <p>
 * <b>Also note: any property changes made AFTER getting the first connection
 * will NOT have any effect!</b>
 */

public class QueueConnectionFactoryBean
extends AbstractConnectionFactoryBean
implements QueueConnectionFactory,
        Externalizable
{
    private transient JtaQueueConnectionFactory factory_;

    // private String xaLookupName_;
    // initially, the xa factory can be looked up in JNDI

    private XAQueueConnectionFactory xaFactory_;
    // the underlying xa instance to delegate to; this should be a
    // serializable bean.

    public QueueConnectionFactoryBean ()
    {
        
       
        factory_ = null;
        xaFactory_ = null;
        // xaLookupName_ = "jms/xaName";
    }

    protected synchronized void checkSetup () throws JMSException
    {
        factory_ = JtaQueueConnectionFactory.getInstance ( resourceName_ );

        if ( factory_ != null )
            return;
        else {
            // ONLY attempt getOrCreate if getInstance fails; to avoid
            // overhead of synchronization of getOrCreate

            XidFactory xidFactory = null;

            xidFactory = new DefaultXidFactory ();

            // try {
            // Context ctx = new InitialContext();
            // xaFactory_ = ( XAQueueConnectionFactory ) ctx.lookup (
            // xaLookupName_ );
            // }
            // catch ( Exception e ) {
            // throw new JMSException ( e.getMessage() );
            // }

            // FOLLOWING CHECK DISABLED BECAUSE NULL IS NOW TAKEN FOR DEFAULT
            // if ( xidFactory_ == null )
            // throw new JMSException (
            // "QueueConnectionFactoryBean: XidFactory not set." );

            if ( xaFactory_ == null && xaFactoryJndiName_.equals ( "" ) )
                throw new JMSException (
                        "QueueConnectionFactoryBean: XAQueueConnectionFactory not set?" );
            if ( !xaFactoryJndiName_.equals ( "" ) ) {
                try {

                    // lookup factory in JNDI
                    Context ctx = new InitialContext ();
                    Context env = (Context) ctx.lookup ( "java:comp/env" );
                    xaFactory_ = (XAQueueConnectionFactory) env
                            .lookup ( xaFactoryJndiName_ );

                } catch ( NamingException ne ) {
                    throw new JMSException (
                            "QueueConnectionFactoryBean: error retrieving factory: "
                                    + ne.getMessage () );
                }
            }

           
            factory_ = JtaQueueConnectionFactory.getOrCreate ( resourceName_,
                    xaFactory_, xidFactory );
            TransactionalResource res = factory_.getTransactionalResource ();
            if ( Configuration.getResource ( res.getName () ) == null )
                Configuration.addResource ( res );
            
            StringBuffer msg = new StringBuffer();
            msg.append ( "QueueConnectionFactoryBean configured with [" );
            msg.append ( "resourceName=" ).append(resourceName_).append (", ");
            msg.append ( "xaFactoryJndiName=" ).append( xaFactoryJndiName_ );
            msg.append ( "]" );
            Configuration.logDebug ( msg.toString() );
            
            Configuration.logWarning ( "WARNING: class " + getClass().getName() + " is deprecated!" );
        }
    }
    

    
   

    /**
     * Get the XAQueueConnectionFactory as previously set.
     * 
     * @return XAQueueConnectionFactory The factory, or null if only the JNDI
     *         name was set.
     */
    public XAQueueConnectionFactory getXaQueueConnectionFactory ()
    {
        return xaFactory_;
    }

    /**
     * Set the XAQueueConnectionFactory to use. This method is optional and an
     * alternative to setXaFactoryJndiName.
     * 
     * @param xaFactory
     *            The object to use.
     */
    public void setXaQueueConnectionFactory ( XAQueueConnectionFactory xaFactory )
    {
        xaFactory_ = xaFactory;
    }

    //
    //
    // IMPLEMENTATION OF QUEUECONNECTIONFACTORY
    //
    //

    public QueueConnection createQueueConnection () throws JMSException
    {
        checkSetup ();
        return factory_.createQueueConnection ();
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
        checkSetup ();
        return factory_.createQueueConnection ( user, pw );
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

 

    public void writeExternal ( ObjectOutput objectOutput ) throws IOException
    {
        // System.out.println ( "Writing resourceName_");
        objectOutput.writeObject ( resourceName_ );
        // System.out.println ( "Writing xaFactory_");
        objectOutput.writeObject ( xaFactory_ );
    }

    public void readExternal ( ObjectInput objectInput ) throws IOException,
            ClassNotFoundException
    {
        // System.out.println ( "reading resourceName_" );
        resourceName_ = (String) objectInput.readObject ();
        // System.out.println ( "reading xaFactory_" );
        try {
            xaFactory_ = (XAQueueConnectionFactory) objectInput.readObject ();
        } catch ( Exception e ) {
            System.err.println ( "Error reading XA Queue Bean" );
            e.printStackTrace ();
            throw new RuntimeException ( e.getMessage () );
        }
        // System.out.println ( "done reading in JMS bean!");
    }


}
