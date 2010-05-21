package com.atomikos.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.transaction.xa.XAResource;

import com.atomikos.datasource.TransactionalResource;
import com.atomikos.datasource.xa.XAResourceTransaction;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.StringHeuristicMessage;
import com.atomikos.icatch.jta.TransactionManagerImp;
import com.atomikos.icatch.system.Configuration;

/**
 * 
 * 
 * Common superclass for the JTA message consumers.
 *
 */

class DefaultJtaMessageConsumer implements HeuristicMessageConsumer 
{

	private MessageConsumer receiver_;
	private TransactionalResource res_;
	private XAResourceTransaction restx_;
	private XAResource xares_;
	
	DefaultJtaMessageConsumer ( MessageConsumer receiver  , TransactionalResource res ,
            XAResource xares )
    {
		receiver_ = receiver;
		res_ = res;
		xares_ = xares;
    }
	
	protected MessageConsumer getMessageConsumer()
	{
		return receiver_;
	}

	/**
	 * Start a resource tx.
	 * 
	 * @param msg
	 *            The heuristic message to use, null if none.
	 * @exception JMSException
	 *                If already enlisted.
	 */
	private synchronized void enlist() throws JMSException 
	{
	    if ( restx_ != null )
	        throw new JMSException (
	                "JtaMessageConsumer.enlist: already enlisted" );
	
	    CompositeTransactionManager ctm = Configuration
	            .getCompositeTransactionManager ();
	
	    if ( ctm == null )
	        throw new JMSException (
	                "JtaMessageConsumer: requires Atomikos TransactionsEssentials to be running! Please make sure to start a transaction first." );
	
	    CompositeTransaction ct = ctm.getCompositeTransaction ();
	    if ( ct == null || ct.getProperty (  TransactionManagerImp.JTA_PROPERTY_NAME ) == null )
	        throw new JMSException (
	                "JTA transaction required for JtaMessageConsumer" );
	
	    restx_ = (XAResourceTransaction) res_.getResourceTransaction ( ct );
	    restx_.setXAResource ( xares_ );
	    restx_.resume ();
	
	}

	/**
	 * End the resource tx.
	 * 
	 * @param msg
	 *            The heuristic message to use, null if none.
	 * @exception JMSException
	 *                If not previously enlisted.
	 */
	private synchronized void delist ( HeuristicMessage msg ) throws JMSException 
	{
	    if ( restx_ == null )
	        throw new JMSException ( "JtaMessageConsumer.delist: not enlisted" );
	    if ( msg != null )
	        restx_.addHeuristicMessage ( msg );
	    restx_.suspend ();
	    restx_ = null;
	}

	/**
	 * @see HeuristicMessageConsumer
	 */
	public Message receive ( HeuristicMessage hmsg ) throws JMSException 
	{
	    Message ret = null;
	    enlist ();
	    try {
	        ret = receiver_.receive ();
	        if ( hmsg == null && ret != null )
	            hmsg = new StringHeuristicMessage (
	                    "Receipt of JMS Message with JMS ID : "
	                            + ret.getJMSMessageID () );
	    } finally {
	        delist ( hmsg );
	    }
	    return ret;
	}

	/**
	 * @see HeuristicMessageConsumer
	 */
	public Message receive ( HeuristicMessage hmsg, long timeout ) throws JMSException 
	{
	    Message ret = null;
	    enlist ();
	    try {
	        ret = receiver_.receive ( timeout );
	    } finally {
	        delist ( hmsg );
	    }
	    return ret;
	}

	/**
	 * @see HeuristicMessageConsumer
	 */
	public Message receiveNoWait ( HeuristicMessage hmsg ) throws JMSException 
	{
	    Message ret = null;
	    enlist ();
	    try {
	        ret = receiver_.receiveNoWait ();
	    } finally {
	        delist ( hmsg );
	    }
	    return ret;
	}

	public Message receiveNoWait() throws JMSException 
	{
	    HeuristicMessage msg = null;
	    return receiveNoWait ( msg );
	}

	public Message receive ( long timeout ) throws JMSException 
	{
	    HeuristicMessage msg = null;
	    return receive ( msg, timeout );
	}

	public String getMessageSelector() throws JMSException 
	{
	    return receiver_.getMessageSelector ();
	}

	public MessageListener getMessageListener() throws JMSException 
	{
	    return receiver_.getMessageListener ();
	}

	public void setMessageListener ( MessageListener l ) throws JMSException 
	{
	    receiver_.setMessageListener ( l );
	}

	public void close() throws JMSException 
	{
	    receiver_.close ();
	}

	public Message receive ( String hmsg ) throws JMSException 
	{
	    StringHeuristicMessage msg = new StringHeuristicMessage ( hmsg );
	    return receive ( msg );
	}

	public Message receive ( String hmsg, long timeout ) throws JMSException 
	{
	    StringHeuristicMessage msg = new StringHeuristicMessage ( hmsg );
	    return receive ( msg, timeout );
	}

	public Message receiveNoWait ( String hmsg ) throws JMSException 
	{
	    StringHeuristicMessage msg = new StringHeuristicMessage ( hmsg );
	    return receiveNoWait ( msg );
	}


    public Message receive () throws JMSException
    {
        HeuristicMessage msg = null;
        return receive ( msg );
    }

	public Message receive(long timeout, String hmsg) throws JMSException {
		return receive ( hmsg , timeout );
	}
	

}
