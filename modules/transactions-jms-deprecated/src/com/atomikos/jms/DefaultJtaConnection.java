package com.atomikos.jms;

import javax.jms.Connection;
import javax.jms.ConnectionConsumer;
import javax.jms.ConnectionMetaData;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.ServerSessionPool;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.XAConnection;
import javax.jms.XASession;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

import com.atomikos.datasource.TransactionalResource;
import com.atomikos.icatch.jta.TransactionManagerImp;
import com.atomikos.icatch.system.Configuration;

/**
 * 
 *
 * Default JMS connection logic.
 *
 */

class DefaultJtaConnection implements Connection 
{
	
	protected static void forceConnectionIntoXaMode ( Connection c )
	{
	   //ORACLE AQ WORKAROUND: 
 	   //force connection into global tx mode
 	   //cf ISSUE 10095
 	   try {
 		   Session s = c.createSession ( true , Session.AUTO_ACKNOWLEDGE );
 		   s.rollback();
 		   s.close();
 	   }
 	   catch ( Exception e ) {
 		   //ignore: workaround code
 		   Configuration.logDebug ( "JMS: driver complains while enforcing XA mode - ignore if no later errors:" , e );
 	   }
	}

	private XAConnection conn_;
	private TransactionalResource res_;
	
	protected static boolean inJtaTransaction() throws JMSException 
	{
		boolean ret = false;
		TransactionManager tm = TransactionManagerImp.getTransactionManager();
		if ( tm != null ) {
			try {
				ret = tm.getStatus() == Status.STATUS_ACTIVE;
			} catch (SystemException e) {
				Configuration.logWarning ( "Could not determine transaction status: " , e );
				JMSException error = new JMSException ( "Could not determine transaction status - see linked exception for more info" );
				error.setLinkedException ( e );
				throw error;
			}
		}
		return ret;
	}

	protected DefaultJtaConnection ( XAConnection c, TransactionalResource res ) 
	{
		conn_ = c;
		res_ = res;
		
	}
	
	protected TransactionalResource getTransactionalResource()
	{
		return res_;
	}

	protected Connection getConnection()
	{
		return conn_;
	}	
	
	public void close() throws JMSException 
	{
	    conn_.close ();
	}

	public void start() throws JMSException 
	{
	    conn_.start ();
	}

	public void setExceptionListener(ExceptionListener l) throws JMSException 
	{
	    conn_.setExceptionListener ( l );
	}

	public ExceptionListener getExceptionListener() throws JMSException 
	{
	    return conn_.getExceptionListener ();
	}

	public ConnectionMetaData getMetaData() throws JMSException 
	{
	    return conn_.getMetaData ();
	}

	public void setClientID(String id) throws JMSException 
	{
	    conn_.setClientID ( id );
	}

	public String getClientID() throws JMSException 
	{
	    return conn_.getClientID ();
	}

	public void stop() throws JMSException 
	{
	    conn_.stop ();
	}

	public ConnectionConsumer createConnectionConsumer (
			Destination dest , String string, ServerSessionPool arg2, int arg3) throws JMSException 
	{
		throw new JMSException ( "Not supported" );
	}

	public ConnectionConsumer createDurableConnectionConsumer(Topic arg0, String arg1, String arg2, ServerSessionPool arg3, int arg4) 
	throws JMSException 
	{
		// TODO check if we should support this
		throw new JMSException ( "Not supported" );
	}

	public Session createSession ( boolean transacted , int ackMode ) 
	throws JMSException {
		
		Session ret = null;
		if ( !transacted && !inJtaTransaction() ) {
            ret = conn_.createSession ( false, ackMode );
        } else {
        	forceConnectionIntoXaMode ( conn_ );
            XASession xasession = conn_.createXASession ();
            ret = new DefaultJtaSession ( xasession, res_,
                    xasession.getXAResource () );
        }

        return ret;
	}

	



}

