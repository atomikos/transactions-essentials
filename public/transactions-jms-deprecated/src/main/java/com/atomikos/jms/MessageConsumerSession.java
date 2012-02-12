/**
 * Copyright (C) 2000-2010 Atomikos <info@atomikos.com>
 *
 * This code ("Atomikos TransactionsEssentials"), by itself,
 * is being distributed under the
 * Apache License, Version 2.0 ("License"), a copy of which may be found at
 * http://www.atomikos.com/licenses/apache-license-2.0.txt .
 * You may not use this file except in compliance with the License.
 *
 * While the License grants certain patent license rights,
 * those patent license rights only extend to the use of
 * Atomikos TransactionsEssentials by itself.
 *
 * This code (Atomikos TransactionsEssentials) contains certain interfaces
 * in package (namespace) com.atomikos.icatch
 * (including com.atomikos.icatch.Participant) which, if implemented, may
 * infringe one or more patents held by Atomikos.
 * It should be appreciated that you may NOT implement such interfaces;
 * licensing to implement these interfaces must be obtained separately from Atomikos.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package com.atomikos.jms;

import com.atomikos.logging.LoggerFactory;
import com.atomikos.logging.Logger;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.Topic;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.icatch.system.Configuration;

/**
 *
 *
 * Common message-driven session functionality.
 *
 */

public abstract class MessageConsumerSession
{
	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = LoggerFactory.createLogger(MessageConsumerSession.class);

	private static final int DEFAULT_TIMEOUT = 30;
	private AbstractConnectionFactoryBean factory;
	private String user;
	private String password;
	private Destination destination;
	private int timeout;
	private boolean notifyListenerOnClose;
	private String messageSelector;
	private boolean daemonThreads;
	private transient MessageListener listener;
	protected transient Thread current;
	private UserTransactionManager tm;
	private boolean active;
	private ExceptionListener exceptionListener;

	protected MessageConsumerSession()
	{
		timeout = DEFAULT_TIMEOUT;
		tm = new UserTransactionManager();

	}

	protected abstract String getSubscriberName();
	protected abstract void setSubscriberName ( String name );
	protected abstract void setNoLocal ( boolean value );

	protected abstract boolean getNoLocal();

	protected void setAbstractConnectionFactoryBean ( AbstractConnectionFactoryBean bean )
	{
		this.factory = bean;
	}

	protected AbstractConnectionFactoryBean getAbstractConnectionFactoryBean()
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
	 * Set the user to create connections with. If the user is not set then the
	 * default connection will be used.
	 *
	 * @param user
	 */
	public void setUser(String user) {
	    this.user = user;
	}

	/**
	 * Get the user to connect with.
	 *
	 * @return The user or null if no explicit authentication is to be used.
	 */
	public String getUser() {
	    return user;
	}

	/**
	 * Set the password to use for connecting. This property only needs to be
	 * set if the User property was also set.
	 *
	 * @param password
	 */
	public void setPassword(String password) {
	    this.password = password;
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
	 * Set the transaction timeout, after which long transactions are rolled
	 * back automatically. The transaction timeout indirectly determines how
	 * long an incoming message can remain in the queue before it is detected by
	 * the listener threads. A smaller value means that listener threads will be
	 * more actively checking the queues, but this implies a faster invalidation
	 * of active transactions due to timeout, and more thread overhead.
	 *
	 * @param seconds
	 *            The timeout for transactions started by the session.
	 */
	public void setTransactionTimeout(int seconds) {
	    this.timeout = seconds;
	}

	/**
	 * Get the transaction timeout in seconds.
	 *
	 * @return
	 */
	public int getTransactionTimeout() {
	    return timeout;
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

	    if ( destination == null )
	        throw new JMSException ( "Please set the Destination first" );
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
	    msg.append ( "user=" ).append( getUser() ).append ( ", " );
	    msg.append ( "password=" ).append ( password ).append ( ", " );
	    msg.append ( "transactionTimeout=" ).append ( getTransactionTimeout() ).append ( ", " );
	    msg.append ( "destination=" ).append( getDestinationName() ).append ( ", " );
	    msg.append ( "notifyListenerOnClose= " ).append( getNotifyListenerOnClose() ).append( ", " );
	    msg.append ( "messageSelector=" ).append( getMessageSelector() ).append( ", " );
	    msg.append ( "daemonThreads=" ).append ( getDaemonThreads() ).append ( ", " );
	    msg.append ( "messageListener=" ).append ( getMessageListener() ).append ( ", " );
	    msg.append ( "exceptionListener=" ).append ( getExceptionListener() ).append ( ", " );
	    msg.append ( "connectionFactory=" ).append ( getAbstractConnectionFactoryBean() );
	    msg.append ( "]" );
	    if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( msg.toString() );

	}

	protected abstract String getDestinationName();

	protected void startNewThread() {
		    if ( active ) {
	        current = new ReceiverThread ();
	        //FIXED 10082
	        current.setDaemon ( daemonThreads );
	        current.start ();
	        if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "MessageConsumerSession: started new thread: " + current );
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

	    current = null;
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

	        private synchronized MessageConsumer refresh () throws JMSException
	        {
	            MessageConsumer ret = null;

	            if ( user != null ) {
	                connection = factory.createConnection ( user, password );

	            } else {
	                connection = factory.createConnection ();
	            }
	            connection.start ();
	            session = connection.createSession ( true, 0 );

	            String subscriberName = getSubscriberName();
	            if ( subscriberName == null ) ret = session.createConsumer ( destination, getMessageSelector () , getNoLocal() );
	            else ret = session.createDurableSubscriber( ( Topic ) destination , subscriberName , getMessageSelector() , getNoLocal() );

	            return ret;
	        }

	        private synchronized void close ()
	        {
	            if ( session != null )
	                try {
	                    session.close ();
	                    session = null;
	                } catch ( JMSException e ) {
	                    if ( Configuration.isInfoLoggingEnabled() ) Configuration.logInfo (
	                            "MessageConsumerSession: Error closing JMS session",
	                            e );
	                    if ( Configuration.isInfoLoggingEnabled() ) Configuration.logInfo ( "MessageConsumerSession: linked exception is " , e.getLinkedException() );
	                }
	            if ( connection != null )
	                try {
	                    connection.close ();
	                    connection = null;
	                } catch ( JMSException e ) {
	                    Configuration
	                            .logInfo (
	                                    "MessageConsumerSession: Error closing JMS connection",
	                                    e );
	                    if ( Configuration.isInfoLoggingEnabled() ) Configuration.logInfo ( "MessageConsumerSession: linked exception is " , e.getLinkedException() );
	                }
	        }

	        public void run ()
	        {
	            MessageConsumer receiver = null;

	            try {
	                // FIRST set transaction timeout, to trigger
	                // TM startup if needed; otherwise the logging
	                // to Configuration will not work!
	                tm.setTransactionTimeout ( timeout );
	            } catch ( SystemException e ) {
	                Configuration
	                        .logWarning (
	                                "MessageConsumerSession: Error in JMS thread while setting transaction timeout",
	                                e );
	            }

	            Configuration
	                    .logInfo ( "MessageConsumerSession: Starting JMS listener thread." );

	            while ( Thread.currentThread () == current ) {

	            	   if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "MessageConsumerSession: JMS listener thread iterating..." );
	                boolean refresh = false;
	                boolean commit = true;
	                try {
	                    Message msg = null;

	                    if ( receiver == null )
	                        receiver = refresh ();

	                    tm.setTransactionTimeout ( timeout );

	                    if ( tm.getTransaction () != null ) {
	                        Configuration
	                                .logWarning ( "MessageConsumerSession: Detected pending transaction: "
	                                        + tm.getTransaction () );
	                        // this is fatal and should not happen due to cleanup in
	                        // previous iteration
	                        // so if it does happen then everything we assumed and
	                        // tried is wrong
	                        // meaning that we can only exit the thread
	                        throw new IllegalStateException (
	                                "Can't reuse listener thread with pending transaction!" );
	                    }

	                    tm.begin ();
	                    // wait for at most half of the tx timeout
	                    msg = receiver.receive ( timeout * 1000 / 2 );

	                    try {

	                        if ( msg != null && listener != null
	                                && Thread.currentThread () == current ) {
	                            Configuration
	                                    .logInfo ( "MessageConsumerSession: Consuming message: "
	                                            + msg.toString () );
	                            listener.onMessage ( msg );
	                            Configuration
	                                    .logDebug ( "MessageConsumerSession: Consumed message: "
	                                            + msg.toString () );
	                        } else {
	                            commit = false;
	                        }
	                    } catch ( Exception e ) {
	                        if ( Configuration.isInfoLoggingEnabled() ) Configuration.logInfo (
	                                "MessageConsumerSession: Error during JMS processing of message "
	                                        + msg.toString () + " - rolling back.",
	                                e );

	                        // This happens if the listener generated the error.
	                        // In that case, don't refresh the connection but rather
	                        // only rollback. There is no reason to assume that the
	                        // connection is corrupted here.
	                        commit = false;
	                    }

	                } catch ( JMSException e ) {
	                    Configuration.logWarning (
	                            "MessageConsumerSession: Error in JMS thread", e );
	                    Exception linkedException = e.getLinkedException();
	                    if ( linkedException != null ) {
	                    	Configuration.logWarning ( "Linked JMS exception is: " , linkedException );
	                    }
	                    // refresh connection to avoid corruption of thread state.
	                    refresh = true;
	                    commit = false;
	                    notifyExceptionListener ( e );

	                } catch ( Exception e ) {
	                    Configuration.logWarning (
	                            "MessageConsumerSession: Error in JMS thread", e );
	                    // Happens if there is an error not generated by the
	                    // listener;
	                    // refresh connection to avoid corruption of thread state.
	                    refresh = true;
	                    commit = false;
	                    JMSException listenerError = new JMSException ( "Unexpected error - please see Atomikos console file for more info" );
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
	                        Configuration
	                                .logDebug (
	                                        "MessageConsumerSession: Error in ending transaction",
	                                        e );
	                    } catch ( HeuristicMixedException e ) {
	                        // thread still OK
	                        Configuration
	                                .logDebug (
	                                        "MessageConsumerSession: Error in ending transaction",
	                                        e );
	                    } catch ( HeuristicRollbackException e ) {
	                        // thread still OK
	                        Configuration
	                                .logDebug (
	                                        "MessageConsumerSession: Error in ending transaction",
	                                        e );
	                    } catch ( Exception e ) {
	                        // fatal since thread tx may still exist
	                        Configuration
	                                .logWarning (
	                                        "MessageConsumerSession: Error ending thread tx association",
	                                        e );

	                        // In this case, we suspend the tx so that it is no
	                        // longer
	                        // associated with this thread. This allows thread reuse
	                        // for
	                        // later messages. If suspend fails, then we can only
	                        // start
	                        // a new thread.
	                        try {
	                            Configuration
	                                    .logDebug ( "MessageConsumerSession: Suspending any active transaction..." );
	                            // try to suspend
	                            tm.suspend ();
	                        } catch ( SystemException err ) {
	                            Configuration
	                                    .logDebug (
	                                            "MessageConsumerSession: Error suspending transaction",
	                                            err );
	                            // start new thread to replace this one, because we
	                            // can't risk a pending transaction
	                            try {
	                                Configuration
	                                        .logDebug ( "MessageConsumerSession: Starting new thread..." );
	                                startNewThread();
	                            } catch ( Exception fatal ) {
	                                // happens if queue or factory no longer set
	                                // in this case, we can't do anything else -
	                                // just let the
	                                // current thread exit and log warning that the
	                                // listener has stopped
	                                Configuration
	                                        .logWarning (
	                                                "MessageConsumerSession: Error starting new thread - stopping listener",
	                                                e );
	                                // set current to null to make this thread exit,
	                                // since reuse is impossible due to risk
	                                // of pending transaction!
	                                stopListening ();
	                            }
	                        }

	                    }

	                    if ( refresh ) {
	                        // close resources here and let the actual refresh be
	                        // done
	                        // by the next iteration
	                        receiver = null;
	                        close ();
	                    }
	                }

	            }
	            Configuration
	                    .logInfo ( "MessageConsumerSession: JMS listener thread exiting." );
	            if ( listener != null && current == null && notifyListenerOnClose ) {
	                // if this session stops listening (no more threads active) then
	                // notify the listener of shutdown by calling with null argument
	                // System.out.println ( "Stopping listener: " + listener );
	                listener.onMessage ( null );
	            }

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
}
