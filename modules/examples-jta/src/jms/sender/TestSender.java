package jms.sender;

import org.activemq.ActiveMQXAConnectionFactory;
import org.activemq.message.ActiveMQQueue;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.jms.AtomikosConnectionFactoryBean;
import com.atomikos.jms.extra.AbstractJmsSenderTemplate;
import com.atomikos.jms.extra.SingleThreadedJmsSenderTemplate;

/**
 * Simple example to illustrate how to use the QueueSenderSession
 * for sending to a queue. Alternatively, you can use an instance
 * of QueueConnectionFactoryBean directly (but you will have to
 * do the cleanup of JMS resources yourself then).
 *
 */

public class TestSender
{

    /**
     * Create a JMS sender template (a managed session for sending).
     * @param url The url of the ActiveMQ broker to connect to.
     * @param qName The name of the queue to send to.
     */
    
    public static SingleThreadedJmsSenderTemplate createSenderTemplate ( String url , String qName )
    throws Exception
    {
    	    //NOTE: you can also use the Atomikos QueueConnectionFactoryBean
    	    //to send messages, but then you have to create and manage connections yourself.
    	SingleThreadedJmsSenderTemplate session = null;
        
        //create and configure an ActiveMQ factory
        ActiveMQXAConnectionFactory xaFactory = new ActiveMQXAConnectionFactory();
        xaFactory.setBrokerURL ( url );

        //create a queue for ActiveMQ
        ActiveMQQueue queue = new ActiveMQQueue();
        queue.setPhysicalName ( qName );

        //setup the Atomikos QueueConnectionFactory for JTA/JMS messaging
        AtomikosConnectionFactoryBean factory = new AtomikosConnectionFactoryBean();
        factory.setXaConnectionFactory ( xaFactory );
        factory.setUniqueResourceName ( qName + "Resource" );

            
        //setup the Atomikos session for sending messages on
        session = new SingleThreadedJmsSenderTemplate();
        session.setAtomikosConnectionFactoryBean ( factory );
        session.setDestination ( queue );
        session.init();
        
        return session;
    }

    /**
     * Send a message in a transaction.
     * @param msg The message to send.
     * @param sender The QueueSenderSession to use.
     */
    
    public static void sendMessageInTransaction ( String text , AbstractJmsSenderTemplate sender )
    throws Exception
    {
        //get a handle to the Atomikos transaction service
        UserTransactionImp userTransaction = new UserTransactionImp();

        userTransaction.setTransactionTimeout ( 120 );
        
        //start a transaction to send in
        userTransaction.begin();
        
        //send a message
        sender.sendTextMessage ( text );

        //commit means send, rollback means cancel
        userTransaction.commit();        
    }

    
    public static void main ( String [] args )
    throws Exception
    {

        String text = "Hello";
        if ( args.length != 0 ) {
            text = args[0];
        }
        else {
            System.out.println ( "Note: you can specify the message text as an argument..." );
        }

        //create a reusable sender session
        SingleThreadedJmsSenderTemplate senderSession = createSenderTemplate ( "tcp://localhost:61616" , "MyQueue" );

        //our sender session is self-maintaining, so the following
        //can be repeated as many times as you like
        sendMessageInTransaction ( text , senderSession );
        System.out.println ( "Sent message with text: " + text );

        //when finished: close the sender session
        senderSession.close();
        
        
	System.exit ( 0 );
    }
}
