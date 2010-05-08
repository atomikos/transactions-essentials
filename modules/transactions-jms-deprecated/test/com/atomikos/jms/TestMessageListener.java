//$Id: TestMessageListener.java,v 1.1.1.1 2006/08/29 10:01:14 guy Exp $
//$Log: TestMessageListener.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:14  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:39  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:34  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:29  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:57  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:21  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.1  2005/01/07 17:07:31  guy
//Added tests for JMS receiver support (lightweigh MDB), and JMS queue bridging.
//
package com.atomikos.jms;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;

import com.atomikos.icatch.jta.UserTransactionManager;

/**
 * 
 * 
 * 
 * 
 *
 * 
 */
public class TestMessageListener 
implements MessageListener, Synchronization
{

	private Message lastMessage;
	private boolean committed = false;
	private boolean simulateError = false;
	private UserTransactionManager utm = new UserTransactionManager();
	private boolean wasClosed = false;
	
	public void setSimulateError ( boolean error )
	{
		simulateError = error;
	}
  
    public void onMessage(Message msg)
    {
    	if ( msg == null ) {
    		wasClosed = true;
    		return;
    		
    	} 
        this.lastMessage = msg;
        try
        {
            Transaction tx = utm.getTransaction();
            try
            {
                tx.registerSynchronization(this);
            }
            catch (IllegalStateException e1)
            {
                e1.printStackTrace();
            }
            catch (RollbackException e1)
            {
                e1.printStackTrace();
            }
        }
        catch (SystemException e)
        {
            
            e.printStackTrace();
        }
        
        committed = false;
        if ( simulateError )
        	throw new RuntimeException ( "Simulated error" );

    }
    
    public Message getLastMessage()
    {
    	return lastMessage;
    }

  
    public void afterCompletion(int state)
    {
        if ( state == Status.STATUS_COMMITTED )
        	committed = true;
        
    }

    public void beforeCompletion()
    {
        
        
    }
    
    public boolean wasCommitted()
    {
    	return committed;
    }
    
    public boolean wasClosed()
    {
    	return wasClosed;
    }

}
