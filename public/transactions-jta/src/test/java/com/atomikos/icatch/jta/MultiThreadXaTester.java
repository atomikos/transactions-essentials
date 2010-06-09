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
