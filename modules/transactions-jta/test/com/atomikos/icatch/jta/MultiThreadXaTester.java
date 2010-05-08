//$Id: MultiThreadXaTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//$Log: MultiThreadXaTester.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:13  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:39  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:33  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:29  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:56  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:19  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.2  2005/05/10 08:44:07  guy
//Merged-in changes from Transactions_2_03 branch.
//
//Revision 1.1.2.1  2004/12/13 19:41:51  guy
//Updated bug fix: synchronized XAResTx map for MM.
//
//Revision 1.1  2004/12/10 05:58:53  guy
//Added synchronization to xaResTxMap (MM Bug).
//
package com.atomikos.icatch.jta;

import javax.transaction.Transaction;
import javax.transaction.xa.XAResource;

import com.atomikos.datasource.xa.TestXAResource;

/**
 * 
 * 
 * 
 * 
 * A test for multi threaded XA on the same Transaction object.
 * 
 * 
 */
public class MultiThreadXaTester implements Runnable
{

	private static int numEnlists = 100;
	
	private static Transaction transaction;
	
	private int activeThreads;
	
	private synchronized void decActiveThreads()
	{
		activeThreads--;
		if ( activeThreads == 0 ) {
			notifyAll();
		}
	}
	
	private synchronized void waitForThreads()
	{
		while ( activeThreads > 0 ) {
			try
			{
				wait();
			}
			catch (InterruptedException e)
			{
               
				e.printStackTrace();
			}	
		}
	}	
   
    public void run()
    {
    	XAResource xares = new TestXAResource();
    	
     	for ( int i = 0 ; i < numEnlists ; i++ ) {
     		try
            {
                transaction.enlistResource ( xares );
                transaction.delistResource ( xares , XAResource.TMSUCCESS );	   
            }
            catch (Exception e)
            {
                
                e.printStackTrace();
            }
     	}
     	
		decActiveThreads();

    }
    
    public static void main ( String[] args )
    throws Exception
    {
    	int numThreads = Integer.parseInt ( args[0] );
    	
    	UserTransactionManager utm = new UserTransactionManager();
    	
    	
		utm.setTransactionTimeout(numThreads);
    	
    	utm.begin();
    	transaction = utm.getTransaction();
    	

		MultiThreadXaTester tester = new MultiThreadXaTester ( numThreads );
		for ( int i = 0 ; i < numThreads ; i++ ) {
			Thread t = new Thread ( tester );
			t.start();
		}
    	
    	tester.waitForThreads();
    	
    	transaction.rollback();
    	
    	System.exit ( 0 );
    	 
    }

    /**
     * @param numThreads
     */
    public MultiThreadXaTester(int numThreads)
    {
        
       	activeThreads = numThreads;
    }

}
