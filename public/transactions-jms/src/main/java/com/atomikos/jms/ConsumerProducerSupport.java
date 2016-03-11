/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jms;

import javax.jms.JMSException;

import com.atomikos.datasource.xa.session.InvalidSessionHandleStateException;
import com.atomikos.datasource.xa.session.SessionHandleState;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.Synchronization;
import com.atomikos.icatch.TxState;
import com.atomikos.icatch.config.Configuration;
import com.atomikos.icatch.jta.TransactionManagerImp;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

 /**
  * Support for common logic in producer and consumer.
  *
  */

abstract class ConsumerProducerSupport 
{
	private static final Logger LOGGER = LoggerFactory.createLogger(ConsumerProducerSupport.class);
	
	private SessionHandleState state;


	protected ConsumerProducerSupport ( SessionHandleState state ) 
	{
		this.state = state;
	}
	

	protected void handleException ( Exception e ) throws AtomikosJMSException 
	{
		state.notifySessionErrorOccurred();
		AtomikosJMSException.throwAtomikosJMSException ( "Error in proxy" , e );
	}
	


	private CompositeTransactionManager getCompositeTransactionManager() 
	{
		CompositeTransactionManager ret = null;
		ret = Configuration.getCompositeTransactionManager();
		return ret;
	}

	
	
	protected void enlist() throws JMSException
	{
		CompositeTransaction ct = null;
		CompositeTransactionManager ctm = getCompositeTransactionManager();
		boolean enlist = false;
		
		if ( ctm != null ) {
			ct = ctm.getCompositeTransaction();
			if ( ct != null && ct.getProperty ( TransactionManagerImp.JTA_PROPERTY_NAME ) != null ) {
				enlist = true;
			}
		}
		
		if ( enlist ) {
			registerSynchronization ( ct );	
			try {
				state.notifyBeforeUse ( ct );
			} catch ( InvalidSessionHandleStateException ex ) {
				String msg = "error during enlist: " + ex.getMessage();
				LOGGER.logWarning ( this + ": " + msg );
				AtomikosJMSException.throwAtomikosJMSException ( msg , ex );
			}
		}
		else {
			String msg = "The JMS session you are using requires a JTA transaction context for the calling thread and none was found." + "\n" +
			"Please correct your code to do one of the following: " + "\n" +			
			"1. start a JTA transaction if you want your JMS operations to be subject to JTA commit/rollback, or" + "\n" + 
			"2. increase the maxPoolSize of the AtomikosConnectionFactoryBean to avoid transaction timeout while waiting for a connection, or" + "\n" +
			"3. create a non-transacted session and do session acknowledgment yourself, or" + "\n" +
			"4. set localTransactionMode to true so connection-level commit/rollback are enabled.";
			LOGGER.logWarning ( this + ": " + msg );
			AtomikosTransactionRequiredJMSException.throwAtomikosTransactionRequiredJMSException ( msg );
		}
		
	}

	private void registerSynchronization ( CompositeTransaction ct ) throws AtomikosJMSException {
		if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": detected transaction " + ct );
		ct.registerSynchronization ( new JmsRequeueSynchronization( ct ) );
	}
	
	
	private class JmsRequeueSynchronization implements Synchronization {
		private static final long serialVersionUID = 1L;
		
		private CompositeTransaction compositeTransaction;
		private boolean afterCompletionDone;

		public JmsRequeueSynchronization ( CompositeTransaction compositeTransaction) {
			this.compositeTransaction = compositeTransaction;
			this.afterCompletionDone = false;
		}

		public void afterCompletion(TxState txState) {
			if ( afterCompletionDone ) return;
			
			if ( txState.isHeuristic() || txState == TxState.TERMINATED ) {
				if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace( "JmsRequeueSynchronization: detected termination of transaction " + compositeTransaction );
				state.notifyTransactionTerminated(compositeTransaction);
				if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace( "JmsRequeueSynchronization: is in terminated state ? " + state.isTerminated() );			
	            afterCompletionDone = true;
	        }	
        	
		}

		public void beforeCompletion() {
		
		}
		
		//override equals: synchronizations for the same tx are equal
		//to avoid receiving double notifications on termination!
		public boolean equals ( Object other )
		{
			boolean ret = false;
			if ( other instanceof JmsRequeueSynchronization ) {
				JmsRequeueSynchronization o = ( JmsRequeueSynchronization ) other;
				ret = this.compositeTransaction.isSameTransaction ( o.compositeTransaction );
			}
		    return ret;
		}
		
		public int hashCode() 
		{
			return compositeTransaction.hashCode();
		}
	}

}
