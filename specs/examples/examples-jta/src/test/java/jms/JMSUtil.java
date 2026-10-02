package jms;

import javax.jms.JMSException;

import org.apache.activemq.ActiveMQXAConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.jms.AtomikosConnectionFactoryBean;
import com.atomikos.jms.extra.AbstractJmsSenderTemplate;
import com.atomikos.jms.extra.MessageDrivenContainer;
import com.atomikos.jms.extra.SingleThreadedJmsSenderTemplate;

public class JMSUtil {


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
    	//XXX : PLQ package change for activeMQ 4.1.2
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

        userTransaction.setTransactionTimeout ( 10 );

        //start a transaction to send in
        userTransaction.begin();

        //send a message
        sender.sendTextMessage ( text );

        //commit means send, rollback means cancel
        userTransaction.commit();
    }



    /**
     * Create a message-driven container for the given broker and queue.
     * @param url The ActiveMQ broker URL to connect to.
     * @param qName The queue to receive from.
     */

    public static MessageDrivenContainer createMessageDrivenContainer ( String url , String qName ) throws JMSException
    {
    	    //NOTE: you can also use the Atomikos QueueConnectionFactoryBean
    	    //with regular JMS sessions, but that means you have to
    	    //do connection management yourself...
    	MessageDrivenContainer pool = null;
    	//XXX : PLQ package change for activeMQ 4.1.2
        //create and configure an ActiveMQ factory
        ActiveMQXAConnectionFactory xaFactory = new ActiveMQXAConnectionFactory();
        xaFactory.setBrokerURL ( url );

        //create a queue for ActiveMQ
        ActiveMQQueue queue = new ActiveMQQueue();
        queue.setPhysicalName ( qName );

        //setup the Atomikos QueueConnectionFactory for JTA/JMS messaging
        AtomikosConnectionFactoryBean factory = new AtomikosConnectionFactoryBean();
        factory.setXaConnectionFactory ( xaFactory );
        factory.setUniqueResourceName ( qName + "ReceiverResource" );


        //setup the Atomikos MessageDrivenContainer to listen for messages
        pool = new MessageDrivenContainer();
        pool.setPoolSize ( 1 );
        pool.setTransactionTimeout ( 10 );
        pool.setNotifyListenerOnClose ( true );
        pool.setAtomikosConnectionFactoryBean( factory );
        pool.setDestination ( queue );
        return pool;

    }

}
