package jms.receiver;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQXAConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;

import com.atomikos.jms.AtomikosConnectionFactoryBean;
import com.atomikos.jms.extra.MessageDrivenContainer;

public class TestListener
implements MessageListener
{

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
        pool.setTransactionTimeout ( 120 );
        pool.setNotifyListenerOnClose ( true );
        pool.setAtomikosConnectionFactoryBean( factory );
        pool.setDestination ( queue );
        return pool;
        
    }
                                                                        
    
    public void onMessage ( Message msg ) {
        
        //here we are if a message is received; a transaction
        //as been started before this method has been called.
        //this is done for us by the MessageDrivenContainer...
        
        if ( msg instanceof TextMessage ) {

            TextMessage tmsg = ( TextMessage ) msg;
            try {
                System.out.println ( "Transactional receive of message: " + tmsg.getText() );
            }
            catch ( JMSException error ) {
                
                error.printStackTrace();
                //throw runtime exception to force rollback of transaction
                throw new RuntimeException ( "Rollback due to error" );
            }
            
        }
        else {
            //not a text message
            System.out.println ( "Transactional receive of message: " + msg );
        }
    }

    public static void main ( String[] args ) throws Exception {
        
    	MessageDrivenContainer container = createMessageDrivenContainer ( "tcp://localhost:61616" , "MyQueue" );

        TestListener listener = new TestListener();
        container.setMessageListener ( listener );
        container.start();
                                                                

        //here, we are certain that the server is listening
        System.out.println ( "Listening for incoming messages..." );
        
    }
}
