package com.atomikos.jms;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TopicSubscriber;
import javax.jms.XASession;

import com.atomikos.beans.PropertyUtils;
import com.atomikos.datasource.xa.XATransactionalResource;
import com.atomikos.datasource.xa.session.SessionHandleState;
import com.atomikos.datasource.xa.session.SessionHandleStateChangeListener;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.system.Configuration;
import com.atomikos.util.ClassLoadingHelper;
import com.atomikos.util.DynamicProxy;

class AtomikosJmsXaSessionProxy extends AbstractJmsSessionProxy implements SessionHandleStateChangeListener
{
	private final static List PRODUCER_CONSUMER_METHODS = Arrays.asList(new String[] {"createConsumer", "createProducer","createDurableSubscriber"});
	private final static List SESSION_TRANSACTION_METHODS = Arrays.asList(new String[] {"commit", "rollback"});
	private final static String CLOSE_METHOD = "close";
	
	public static Object newInstance ( XASession s , XATransactionalResource jmsTransactionalResource , 
			SessionHandleStateChangeListener pooledConnection , SessionHandleStateChangeListener connectionProxy ) throws JMSException 
	{
        AtomikosJmsXaSessionProxy proxy = new AtomikosJmsXaSessionProxy ( s , jmsTransactionalResource , pooledConnection , connectionProxy );
        Set interfaces = PropertyUtils.getAllImplementedInterfaces ( s.getClass() );
        //see case 24532
        interfaces.add ( DynamicProxy.class );
        Class[] interfaceClasses = ( Class[] ) interfaces.toArray ( new Class[0] );
        
        List classLoaders = new ArrayList();
		classLoaders.add ( Thread.currentThread().getContextClassLoader() );
		classLoaders.add ( s.getClass().getClassLoader() );
		classLoaders.add ( AtomikosJmsXaSessionProxy.class.getClassLoader() );
		
		return ( Session ) ClassLoadingHelper.newProxyInstance ( classLoaders , interfaceClasses , proxy );
    }


	
	private XASession delegate;
	private boolean closed = false;
	private SessionHandleState state;
	private XATransactionalResource jmsTransactionalResource;
	
	private AtomikosJmsXaSessionProxy ( XASession s , XATransactionalResource jmsTransactionalResource , 
			SessionHandleStateChangeListener pooledConnection , SessionHandleStateChangeListener connectionProxy ) 
	{
		this.delegate = s;
		this.jmsTransactionalResource = jmsTransactionalResource;
		this.state = new SessionHandleState ( jmsTransactionalResource , s.getXAResource() );
		state.registerSessionHandleStateChangeListener ( pooledConnection );
		state.registerSessionHandleStateChangeListener ( connectionProxy );
		state.registerSessionHandleStateChangeListener ( this );
		//for JMS, session borrowed corresponds to creation of the session
		state.notifySessionBorrowed();
	}
	
	public Object invoke ( Object proxy, Method method, Object[] args ) throws JMSException 
	{
		String methodName = method.getName();
		
		//see case 24532
		if ( methodName.equals ( "getInvocationHandler" ) ) return this;
		
		synchronized (this) {
			if (closed) {
				if (!methodName.equals(CLOSE_METHOD)) {
					String msg = "Session was closed already - calling " + methodName + " is no longer allowed.";
					Configuration.logWarning ( this + ": " + msg );
					throw new javax.jms.IllegalStateException( msg );
				}
				return null;
			}
			
			if ( SESSION_TRANSACTION_METHODS.contains ( methodName ) ) {
				String msg = "Calling commit/rollback is not allowed on a managed session!";
				// When using the Spring PlatformTransactionManager, there is always a call to commit on the Session in a synchronization's afterCompletion.
				// The PlatformTransactionManager uses that mechanism for non-JTA TX commit which happens because DefaultMessageListenerContainer.sessionTransacted
				// must be set to true. Spring catches TransactionInProgressException in case of JTA TX management.
				// This is fine except that we used to log this message at warning level when this happens which is annoying as it repeats once per TX -> lowered it to info.
				//
				// See: org.springframework.jms.connection.ConnectionFactoryUtils$JmsResourceSynchronization.afterCommit()
				// and org.springframework.jms.connection.JmsResourceHolder.commitAll() (as of Spring 2.0.8)
				Configuration.logInfo ( this + ": " + msg );
				throw new javax.jms.TransactionInProgressException ( msg );
			}
			
			
			if ( CLOSE_METHOD.equals ( methodName ) ) {
				state.notifySessionClosed();
				Configuration.logInfo ( this + ": closing session " + this + " - is terminated ? " + state.isTerminated() );
				if ( state.isTerminated() ) {
					//only destroy if there is no pending 2PC - otherwise this is done
					//in the registered synchronization
					destroy();
				}
			}
			
			if (PRODUCER_CONSUMER_METHODS.contains(methodName)) {
				Configuration.logInfo ( this + ": calling " + methodName + " on JMS driver session " + delegate );
				Object producerConsumerProxy = null;
				if ( "createConsumer".equals ( methodName ) ) {
					MessageConsumer vendorConsumer = null;
					try {
						vendorConsumer = ( MessageConsumer ) method.invoke ( delegate , args);
					} catch ( Exception e ) {
						String msg = "Failed to create MessageConsumer: " + e.getMessage();
						state.notifySessionErrorOccurred();
						convertProxyError ( e , msg );
					}
					producerConsumerProxy = new AtomikosJmsMessageConsumerProxy ( vendorConsumer , state );
				}
				else if ( "createProducer".equals( methodName ) ) {
					MessageProducer vendorProducer = null;
					try {
						vendorProducer = ( MessageProducer ) method.invoke ( delegate , args);
					} catch ( Exception e ) {
						String msg = "Failed to create MessageProducer: " + e.getMessage();
						state.notifySessionErrorOccurred();
						convertProxyError ( e , msg );
					}
					producerConsumerProxy = new AtomikosJmsMessageProducerProxy ( vendorProducer , state );
				}
				else if ( "createDurableSubscriber".equals ( methodName ) ) {
					TopicSubscriber vendorSubscriber = null;
					try {
						vendorSubscriber = ( TopicSubscriber ) method.invoke ( delegate , args);
					} catch ( Exception  e ) {
						String msg = "Failed to create durable TopicSubscriber: " + e.getMessage();
						state.notifySessionErrorOccurred();
						convertProxyError ( e , msg );
					}
					producerConsumerProxy = new AtomikosJmsTopicSubscriberProxy ( vendorSubscriber , state );
				}
				Configuration.logDebug ( this + ": " + methodName + " returning " + producerConsumerProxy );
				return producerConsumerProxy;
			
			}
			
			try {
				Configuration.logInfo ( this + ": calling " + methodName + " on JMS driver session..." );
				Object ret = method.invoke(delegate, args);
				Configuration.logDebug ( this + ": " + methodName + " returning " + ret );
				return ret;
			}  catch (Exception ex) {
				String msg =  "Error delegating call to " + methodName + " on JMS driver";
				state.notifySessionErrorOccurred();
				convertProxyError ( ex , msg );
			}
			
		} // synchronized (this)
		
		//dummy return to keep compiler happy
		return null;
	}
	


	protected void destroy() {
		try {
			Configuration.logDebug ( this + ": destroying JMS session " + this );
			if ( !closed ) {
				closed = true;
				delegate.close(); 
			}
		} catch  ( JMSException e ) {
			Configuration.logWarning ( this + ": could not close JMS session" , e );
		}
	}


	protected boolean isAvailable() {
		boolean ret = false;
		if ( state != null ) ret = state.isTerminated();
		return ret;
	}


	protected boolean isErroneous() {
		boolean ret = false;
		if ( state != null ) ret = state.isErroneous();
		return ret;
	}


	protected boolean isInTransaction ( CompositeTransaction ct ) {
		boolean ret = false;
		if ( state != null ) ret = state.isActiveInTransaction ( ct );
		return ret;
	}
	
	protected boolean isInactiveTransaction ( CompositeTransaction ct )
	{
		boolean ret = false;
		if ( state != null ) ret = state.isInactiveInTransaction ( ct );
		return ret;
	}

	public void onTerminated() {
		destroy();
	}
	

	public String toString()
	{
		return "atomikos xa session proxy for resource " + jmsTransactionalResource.getName();
	}



}
