package jms;


import javax.jms.MapMessage;
import javax.jms.QueueConnection;
import javax.jms.QueueSender;
import javax.jms.QueueSession;

import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.spring.ActiveMQConnectionFactory;


 /**
  * This class orders a withdrawal from the bank
  * by sending a JMS message to the queue.
  * Note: this class does not have to use
  * Atomikos Transactions because it only
  * accesses one resource (the queue) and
  * can therefore work perfectly within a
  * transacted JMS session. Also, this class
  * does not have to use Spring (although it could).
  * JMS allows loose coupling between client
  * and server.
  */

public class OrderWithdrawal
{
    public static void main ( String[] args )
    throws Exception
    {
        int port = 61616;
        if ( args.length > 0 ) {
            port = Integer.parseInt ( args[0] );
        }

    

        String url = "tcp://localhost:" + port;
        ActiveMQQueue queue = new ActiveMQQueue();
        queue.setPhysicalName ( "BANK_QUEUE" );
        ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
        cf.setBrokerURL ( url );
        QueueConnection c = null;
        try {
            c = cf.createQueueConnection();
        }
        catch ( Exception e ) {
            System.out.println ( "COULD NOT CONNECT - LAUNCH 'RUN' FIRST!" );
            System.exit ( 1 );
        }
        QueueSession session = c.createQueueSession ( true , 0 );
        QueueSender sender = session.createSender ( queue );
        MapMessage m = session.createMapMessage();
        
        m.setIntProperty ( "account" , 10 );
        m.setIntProperty ( "amount" , 100 );
        sender.send ( m );
        session.commit();
        session.close();
        c.close();
        
    }
    
}
