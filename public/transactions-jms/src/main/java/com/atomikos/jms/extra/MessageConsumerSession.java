/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jms.extra;

import java.util.HashMap;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.jms.AtomikosConnectionFactoryBean;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

/**
 *
 *
 * Common message-driven session functionality.
 *
 */

class MessageConsumerSession
{
	private static final Logger LOGGER = LoggerFactory.createLogger(MessageConsumerSession.class);

	private AtomikosConnectionFactoryBean factory;
	private Destination destination;
	private String destinationName;
	private MessageConsumerSessionProperties properties;
	private boolean notifyListenerOnClose;
	private String messageSelector;
	private boolean daemonThreads;
	private transient MessageListener listener;
	protected transient ReceiverThread current;
	private UserTransactionManager tm;
	private boolean active;
	private ExceptionListener exceptionListener;

	//for durable subscribers only
	private boolean noLocal;
	private String subscriberName;
	private String clientID;

	private Map<String,Long> messageCounterMap = new HashMap<>();

	protected MessageConsumerSession ( MessageConsumerSessionProperties properties )
	{
		this.properties = properties;
		tm = new UserTransactionManager();
		noLocal = false;
		subscriberName = null;
	}

	protected String getSubscriberName()
	{
		return subscriberName;
	}

	protected void setSubscriberName ( String name )
	{
		this.subscriberName = name;
	}

	protected  void setNoLocal ( boolean value )
	{
		this.noLocal = value;
	}

	protected boolean getNoLocal()
	{
		return noLocal;
	}

	protected void setAtomikosConnectionFactoryBean ( AtomikosConnectionFactoryBean bean )
	{
		this.factory = bean;
	}

	protected AtomikosConnectionFactoryBean getAtomikosConnectionFactoryBean()
	{
		return factory;
	}

	/**
	 * Sets whether threads should be daemon threads or not.
	 * Default is false.
	 * @param value If true then threads will be daemon threads.
	 */
	public void setDaemonThreads ( boolean value )
	{
			this.daemonThreads = value;
	}

	/**
	 * Tests whether threads are daemon threads.
	 * @return True if threads are deamons.
	 */
	public boolean getDaemonThreads()
	{
			return daemonThreads;
	}

	/**
	 * Get the message selector (if any)
	 *
	 * @return The selector, or null if none.
	 */
	public String getMessageSelector()
	{
	    return messageSelector;
	}

	/**
	 * Set the message selector to use.
	 *
	 * @param selector
	 */
	public void setMessageSelector(String selector)
	{
	    this.messageSelector = selector;
	}

	/**
	 * Gets the destination.
	 *
	 * @return Null if none was set.
	 */
	public Destination getDestination()
	{
		return destination;
	}

	/**
	 * Sets the destination to listen on.
	 * @param destination
	 */
	public void setDestination ( Destination destination )
	{
		this.destination = destination;
	}



	/**
	 * Get the transaction timeout in seconds.
	 *
	 * @return
	 */
	public int getTransactionTimeout() {
	    return properties.getTransactionTimeout();
	}

	/**
	 * Set the message listener for this session. Only one message listener per
	 * session is allowed. After this method is called, the listener will
	 * receive incoming messages in its onMessage method, in a JTA transaction.
	 * By default, the receiver will commit the transaction unless the onMessage
	 * method throws a runtime exception (in which case rollback will happen).
	 *
	 * If no more messages are desired, then this method should be called a
	 * second time with a null argument.
	 *
	 * @param listener
	 */
	public void setMessageListener(MessageListener listener) {
	    this.listener = listener;
	}

	/**
	 * Get the message listener of this session, if any.
	 *
	 * @return
	 */
	public MessageListener getMessageListener() {
	    return listener;
	}

	/**
	 * Start listening for messages.
	 *
	 */
	public void startListening() throws JMSException, SystemException {

		if ( active ) throw new IllegalStateException ( "MessageConsumerSession: startListening() called a second time without stopListening() in between" );

	    if ( getDestinationName() == null )
	        throw new JMSException ( "Please set property 'destination' or 'destinationName' first" );
	    if ( factory == null )
	        throw new JMSException (
	                "Please set the ConnectionFactory first" );


	    tm.setStartupTransactionService ( true );
	    tm.init();
	    //disable startup to avoid threads re-start the core
	    //during shutdown!!! (see ISSUE 10084)
	    tm.setStartupTransactionService ( false );
	    active = true;
	    startNewThread();

	    StringBuffer msg = new StringBuffer();
	    msg.append ( "MessageConsumerSession configured with [" );
	    msg.append ( "transactionTimeout=" ).append ( getTransactionTimeout() ).append ( ", " );
	    msg.append ( "destination=" ).append( getDestinationName() ).append ( ", " );
	    msg.append ( "notifyListenerOnClose= " ).append( getNotifyListenerOnClose() ).append( ", " );
	    msg.append ( "messageSelector=" ).append( getMessageSelector() ).append( ", " );
	    msg.append ( "daemonThreads=" ).append ( getDaemonThreads() ).append ( ", " );
	    msg.append ( "messageListener=" ).append ( getMessageListener() ).append ( ", " );
	    msg.append ( "exceptionListener=" ).append ( getExceptionListener() ).append ( ", " );
	    msg.append ( "connectionFactory=" ).append ( getAtomikosConnectionFactoryBean() );
	    msg.append ( "]" );
	    if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( msg.toString() );

	}

	/**
	 * Gets the destination name, either as set directly or
	 * as set by the destinationName property.
	 *
	 * @return The destination's provider-specific name, or null if none set.
	 */
	public String getDestinationName()
	{
		String ret = destinationName;
		if ( ret == null ) {
			if ( destination instanceof Queue ) {
				Queue q = ( Queue ) destination;
				try {
					ret = q.getQueueName();
				} catch (JMSException e) {
					if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( "Error retrieving queue name" , e );
				}
			} else if ( destination instanceof Topic ) {
				Topic t = ( Topic ) destination;
				try {
					ret = t.getTopicName();
				} catch (JMSException e) {
					if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( "Error retrieving topic name" , e );
				}
			}
		}
		return ret;
	}

	/**
	 * Sets the provider-specific destination name. Required, unless
	 * setDestination is called instead.
	 *
	 * @param destinationName
	 */
	public void setDestinationName ( String destinationName )
	{
		this.destinationName = destinationName;
	}

	protected void startNewThread() {
		    if ( active ) {
	        current = new ReceiverThread ();
	        //FIXED 10082
	        current.setDaemon ( daemonThreads );
	        current.start ();
	        if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( "MessageConsumerSession: started new thread: " + current );
		    }
		    //if not active: ignore
	}

	private synchronized void notifyExceptionListener ( JMSException e )
	{
		if ( exceptionListener != null ) exceptionListener.onException ( e );
	}

	/**
	 * Stop listening for messages. If <b>notifyListenerOnClose</b> is set then
	 * calling this method will indirectly lead to the invocation of the
	 * listener's onMessage method with a null argument (and without a
	 * transaction). This allows receivers to detect shutdown.
	 *
	 */
	public void stopListening() {

		if ( current != null ) {
			ReceiverThread t = current;
			// cf issue 62452: next, FIRST set current to null
			// to allow listener thread to exit
			// needed because the subsequent JMS cleanup will wait
			// for the listener thread to finish!!!
			current = null;
			t.closeJmsResources ( true );
		}
	    tm.close();
	    active = false;
	}

	/**
	 *
	 * Check wether the session is configured to notify the listener upon close.
	 *
	 * @return boolean If true then the listener will receive a null message
	 *         when the session is closed.
	 *
	 */
	public boolean getNotifyListenerOnClose() {
	    return notifyListenerOnClose;
	}

	/**
	 * Set whether the listener should be notified on close.
	 *
	 * @param b
	 */
	public void setNotifyListenerOnClose(boolean b) {
	    notifyListenerOnClose = b;
	}

	  class ReceiverThread extends Thread
	    {
	        private Connection connection;
	        private Session session;

	        private ReceiverThread ()
	        {
	        }

	        private synchronized MessageConsumer refreshJmsResources () throws JMSException
	        {
	            MessageConsumer ret = null;
	            
                connection = factory.createConnection ();
	            
	            if (clientID != null ) {
	            	//see http://activemq.apache.org/virtual-destinations.html
	            	String connectionClientID = connection.getClientID();
	            	if ( connectionClientID == null ) connection.setClientID(clientID);
	            	else LOGGER.logWarning ( "Reusing connection with preset clientID: " + connectionClientID );
	            }

	            connection.start ();

	            session = connection.createSession ( true, 0 );
	            if ( getDestination() == null ) {
	            	Destination d = DestinationHelper.findDestination ( getDestinationName() , session );
	            	setDestination ( d );
	            }

	            String subscriberName = getSubscriberName();
	            if ( subscriberName == null )  {
	            	// cf case 33305: only use the noLocal flag if the destination is a topic
	            	if ( destination instanceof Topic ) {
	            		// topic -> use noLocal
	            		ret = session.createConsumer ( destination, getMessageSelector () , getNoLocal() );
	            	}
	            	else {
	            		// queue -> noLocal flag not defined in JMS 1.1!
	            		ret = session.createConsumer ( destination , getMessageSelector() );
	            	}
	            }
	            else {
	            	// subscriberName is not null -> topic -> use noLocal flag too
	            	ret = session.createDurableSubscriber( ( Topic ) destination , subscriberName , getMessageSelector() , getNoLocal() );
	            }

	            return ret;
	        }

	        private synchronized void closeJmsResources ( boolean threadWillStop )
	        {
	            try {
					if ( session != null ) {

						if ( threadWillStop ) {

							try {
								LOGGER.logInfo ( "MessageConsumerSession: unsubscribing " + subscriberName + "...");
								if ( Thread.currentThread() != this ) {

									//see case 62452 and 80464: wait for listener thread to exit so the subscriber is no longer in use
									if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "MessageConsumerSession: waiting for listener thread to finish..." );
									this.join();
									if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( "MessageConsumerSession: waiting done." );

								}

								if (subscriberName != null && properties.getUnsubscribeOnClose()) {
									LOGGER.logInfo ( "MessageConsumerSession: unsubscribing " + subscriberName + "...");
									session.unsubscribe ( subscriberName );
								}

							} catch ( JMSException e ) {

								 if ( LOGGER.isDebugEnabled() ) {
									 LOGGER.logDebug ("MessageConsumerSession: Error closing on JMS session", e );
									 LOGGER.logDebug ( "MessageConsumerSession: linked exception is " , e.getLinkedException() );
								 }
							}
						}

					    try {
					    	if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "MessageConsumerSession: closing JMS session..." );
					        session.close ();
					        session = null;
					        if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( "MessageConsumerSession: JMS session closed." );
					    } catch ( JMSException e ) {
					        if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "MessageConsumerSession: Error closing JMS session", e );
					        if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "MessageConsumerSession: linked exception is " , e.getLinkedException() );
					    }
					}
					if ( connection != null )
					    try {
					    	if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "MessageConsumerSession: closing JMS connection..." );
					        connection.close ();
					        connection = null;
					        if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( "MessageConsumerSession: JMS connection closed." );
					    } catch ( JMSException e ) {
					    	LOGGER.logWarning ( "MessageConsumerSession: Error closing JMS connection", e );
					        LOGGER.logWarning ( "MessageConsumerSession: linked exception is " , e.getLinkedException() );
					    }
				} catch ( Throwable e ) {
					LOGGER.logWarning ( "MessageConsumerSession: Unexpected error during close: " , e );
					//DON'T rethrow
				}
	        }

	        public void run ()
	        {
	            MessageConsumer receiver = null;

	            try {
	                // FIRST set transaction timeout, to trigger
	                // TM startup if needed; otherwise the logging
	                // to Configuration will not work!
	                tm.setTransactionTimeout ( getTransactionTimeout() );
	            } catch ( SystemException e ) {
	            	LOGGER.logError ( "MessageConsumerSession: Error in JMS thread while setting transaction timeout", e );
	            }

	            LOGGER.logDebug ( "MessageConsumerSession: Starting JMS listener thread." );

	            while ( Thread.currentThread () == current ) {

	            	   if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( "MessageConsumerSession: JMS listener thread iterating..." );
	                boolean refresh = false;
	                boolean commit = true;
	                try {
	                    Message msg = null;

	                    while ( receiver == null ) {
	                    	try {
	                    		receiver = refreshJmsResources ();
	                    	} catch ( JMSException connectionGone ) {
	                    		LOGGER.logWarning ( "Error refreshing JMS connection" , connectionGone );
	                    		closeJmsResources(false);
	                    		// wait a while to avoid OutOfMemoryError with MQSeries
	                    		// cf case 73406
	                    		Thread.sleep ( getTransactionTimeout() * 1000 / 4 );
	                    	}
	                    }


	                    tm.setTransactionTimeout ( getTransactionTimeout() );

	                    if ( tm.getTransaction () != null ) {
	                    	LOGGER.logFatal ( "MessageConsumerSession: detected pending transaction: " + tm.getTransaction () );
	                        // this is fatal and should not happen due to cleanup in previous iteration
	                        throw new IllegalStateException ( "Can't reuse listener thread with pending transaction!" );
	                    }

	                    tm.begin ();
	                    msg = receiveNextMessage(receiver);

	                    try {

	                        if ( msg != null && listener != null && Thread.currentThread () == current ) {
	                        	processMessage(msg);
	                        } else {
	                            commit = false;
	                        }
	                    } catch ( Exception e ) {
	                        if ( LOGGER.isDebugEnabled() ) {
	                        	LOGGER.logDebug ("MessageConsumerSession: Error during JMS processing of message "
	                        					+ msg.toString () + " - rolling back.", e );
	                        }

	                        // This happens if the listener generated the error.
	                        // In that case, don't refresh the connection but rather
	                        // only rollback. There is no reason to assume that the
	                        // connection is corrupted here.
	                        commit = false;
	                    }

	                } catch ( JMSException e ) {
	                    LOGGER.logWarning ( "MessageConsumerSession: Error in JMS thread", e );
	                    Exception linkedException = e.getLinkedException();
	                    if ( linkedException != null ) {
	                    	LOGGER.logWarning ( "Linked JMS exception is: " , linkedException );
	                    }
	                    // refresh connection to avoid corruption of thread state.
	                    refresh = true;
	                    commit = false;
	                    notifyExceptionListener ( e );

	                } catch ( Throwable e ) {
	                    LOGGER.logError ("MessageConsumerSession: Error in JMS thread", e );
	                    // Happens if there is an error not generated by the listener;
	                    // refresh connection to avoid corruption of thread state.
	                    refresh = true;
	                    commit = false;
	                    JMSException listenerError = new JMSException ( "Unexpected error - please see application log for more info" );
	                    notifyExceptionListener ( listenerError );

	                } finally {

	                    // Make sure no tx exists for thread, or we can't reuse
	                    // the thread for later transactions!
	                    try {
	                        if ( commit )
	                            tm.commit ();
	                        else {
	                            tm.rollback ();
	                        }
	                    } catch ( RollbackException e ) {
	                        // thread still OK
	                    	LOGGER.logWarning ( "MessageConsumerSession: Error in ending transaction", e );
	                    } catch ( HeuristicMixedException e ) {
	                        // thread still OK
	                    	LOGGER.logWarning ( "MessageConsumerSession: Error in ending transaction", e );
	                    } catch ( HeuristicRollbackException e ) {
	                        // thread still OK
	                    	LOGGER.logWarning ( "MessageConsumerSession: Error in ending transaction", e );
	                    } catch ( Throwable e ) {
	                    	LOGGER.logWarning ("MessageConsumerSession: Error ending thread tx association", e );
	                        // In this case, we suspend the tx so that it is no longer associated with this thread. 
	                    	// This allows thread reuse for later messages. 
	                    	// If suspend fails, then we can only start a new thread.
	                        try {
	                        	LOGGER.logTrace ( "MessageConsumerSession: Suspending any active transaction..." );
	                            tm.suspend ();
	                        } catch ( SystemException err ) {
	                        	LOGGER.logError ( "MessageConsumerSession: Error suspending transaction", err );
	                            // start new thread to replace this one, because we can't risk a pending transaction
	                            try {
	                            	LOGGER.logTrace ( "MessageConsumerSession: Starting new thread..." );
	                                startNewThread();
	                            } catch ( Throwable fatal ) {
	                                // happens if queue or factory no longer set
	                                // in this case, we can't do anything else -
	                                // just let the current thread exit and log 
	                            	LOGGER.logFatal ( "MessageConsumerSession: Error starting new thread - stopping listener", e );
	                                // set current to null to make this thread exit, 
	                            	// since reuse is impossible due to risk of pending transaction!
	                                stopListening ();
	                            }
	                        }

	                    }

	                    if ( refresh && Thread.currentThread () == current) {
	                        // close resources here and let the actual refresh be done by the next iteration
	                    	try {
	                    		receiver.close();
	                    	} catch ( Throwable e ) {
	                    		if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( "MessageConsumerSession: Error closing receiver" , e );
	                    	}
	                        receiver = null;
	                        closeJmsResources ( false );
	                    }
	                }

	            }

                // thread is going to die, close the receiver here.
	            if(receiver != null) {
	              try {
                    receiver.close();
                  } catch ( Throwable e ) {
                    LOGGER.logTrace ( "MessageConsumerSession: Error closing receiver" , e );
                  }
                  receiver = null;
	            }

	            LOGGER.logDebug ( "MessageConsumerSession: JMS listener thread exiting." );
	            if ( listener != null && current == null && notifyListenerOnClose ) {
	                // if this session stops listening (no more threads active) then
	                // notify the listener of shutdown by calling with null argument
	                listener.onMessage ( null );
	            }

	        }




	    }
	  
	  private void cleanRedeliveryLimit(Message msg) throws JMSException {
		  messageCounterMap.remove(msg.getJMSMessageID());
	  }	  

	  private void checkRedeliveryLimit(Message msg) throws JMSException {
		  if (msg.getJMSRedelivered()) {
			String key = msg.getJMSMessageID();
			Long redeliveryCount = messageCounterMap.get(key);
			if (redeliveryCount == null) {
				redeliveryCount = 1L;
			} else {
				redeliveryCount++;
				if (redeliveryCount > 5) {
					LOGGER.logWarning("Possible poison message detected - check https://www.atomikos.com/Documentation/PoisonMessage: " + msg.toString());
				}
			}
			messageCounterMap.put(key, redeliveryCount);
		}
	  }

	/**
	 * Gets the exception listener (if any).
	 * @return Null if no ExceptionListener was set.
	 */
	public ExceptionListener getExceptionListener()
	{
		return exceptionListener;
	}

	/**
	 * Sets the exception listener. The listener will be
	 * notified of connection-level JMS errors.
	 * <b>IMPORTANT:</b> exception listeners will NOT be
	 * notified of any errors thrown by the MessageListener.
	 * Instead, the ExceptionListener mechanism is meant
	 * for system-level connectivity errors towards and from
	 * the underlying message system.
	 *
	 * @param exceptionListener
	 */
	public void setExceptionListener ( ExceptionListener exceptionListener )
	{
		this.exceptionListener = exceptionListener;
	}

	public void setClientID(String clientID) {
		this.clientID = clientID;
	}

	/**
	 * Gets the receive timeout in seconds.
	 *
	 * @return
	 */
	public int getReceiveTimeout() {
		return properties.getReceiveTimeout();
	}
	
	protected Message receiveNextMessage(MessageConsumer receiver) throws JMSException {
		// wait for at most half of the tx timeout
		// cf case 83599: use separate timeout for receive to speedup shutdown
		return receiver.receive ( getReceiveTimeout() * 1000 );
	}
	
	protected void processMessage(Message msg) throws JMSException {
		LOGGER.logDebug ( "MessageConsumerSession: Consuming message: " + msg.toString () );
		checkRedeliveryLimit(msg);
		listener.onMessage ( msg );
		LOGGER.logTrace ( "MessageConsumerSession: Consumed message: " + msg.toString () );
		cleanRedeliveryLimit(msg);
	}
}
