/**
 * Copyright (C) 2000-2012 Atomikos <info@atomikos.com>
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

import java.util.HashMap;
import java.util.Map;

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
	


	protected CompositeTransactionManager getCompositeTransactionManager() 
	{
		CompositeTransactionManager ret = null;
		ret = Configuration.getCompositeTransactionManager();
		return ret;
	}

	
	
	protected void enlist ( String hmsg ) throws JMSException
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
		if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": detected transaction " + ct );
		ct.registerSynchronization ( new JmsRequeueSynchronization( ct ) );
	}
	
	
	private class JmsRequeueSynchronization implements Synchronization {
		private static final long serialVersionUID = 1L;
		
		
		private CompositeTransaction compositeTransaction;
		private boolean afterCompletionDone;
		private Map<TxState,Object> transactionStatesIndicatingConnectionReusability;

		public JmsRequeueSynchronization ( CompositeTransaction compositeTransaction) {
			this.compositeTransaction = compositeTransaction;
			this.afterCompletionDone = false;
			transactionStatesIndicatingConnectionReusability = new HashMap<TxState,Object>();
			transactionStatesIndicatingConnectionReusability.put(TxState.TERMINATED,TxState.TERMINATED);
			transactionStatesIndicatingConnectionReusability.put(TxState.HEUR_ABORTED,TxState.HEUR_ABORTED);
			transactionStatesIndicatingConnectionReusability.put(TxState.HEUR_COMMITTED,TxState.HEUR_COMMITTED);
			transactionStatesIndicatingConnectionReusability.put(TxState.HEUR_HAZARD,TxState.HEUR_HAZARD);
			transactionStatesIndicatingConnectionReusability.put(TxState.HEUR_MIXED,TxState.HEUR_MIXED);
		}

		public void afterCompletion(Object txstate) {
			if ( afterCompletionDone ) return;
			
			if ( transactionStatesIndicatingConnectionReusability.containsKey(txstate) ) {
				if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug( "JmsRequeueSynchronization: detected termination of transaction " + compositeTransaction );
				state.notifyTransactionTerminated(compositeTransaction);
				if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug( "JmsRequeueSynchronization: is in terminated state ? " + state.isTerminated() );			
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
