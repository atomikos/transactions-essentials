package com.atomikos.icatch.imp;

import com.atomikos.icatch.TestTSListener;
import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.system.Configuration;

public abstract class AbstractJUnitTSListenerTest 
extends TransactionServiceTestCase {

	private UserTransactionService uts;
    
    private TestTSListener listener;
   
	
	public AbstractJUnitTSListenerTest(String name) {
		super(name);
	}
	
	protected abstract UserTransactionService init();
	
    protected void setUp ()
    {
        super.setUp ();
        uts = init();
        listener = new TestTSListener();
        
        
    }
    
    protected void tearDown()
    {
        
        super.tearDown();
    }
    
    public void testCallbacksOkIfRegisteredInUts()
    {
    		uts.registerTSListener ( listener );
    		uts.init ( uts.createTSInitInfo() );
    		assertFalse ( listener.isShutdownAfter() );
    		assertFalse ( listener.isShutdownBefore() );
    		assertTrue ( listener.isInitedBefore() );
    		assertTrue ( listener.isInitedAfter() );
    		
    		uts.shutdown(true);
    		assertTrue ( listener.isShutdownAfter() );
    		assertTrue ( listener.isShutdownBefore() );
    }
    
    public void testCallbacksOkIfRegisteredInConfiguration()
    {
		Configuration.addTSListener ( listener );
		uts.init ( uts.createTSInitInfo() );
		assertFalse ( listener.isShutdownAfter() );
		assertFalse ( listener.isShutdownBefore() );
		assertTrue ( listener.isInitedBefore() );
		assertTrue ( listener.isInitedAfter() );
		
		uts.shutdown(true);
		assertTrue ( listener.isShutdownAfter() );
		assertTrue ( listener.isShutdownBefore() );
    }
    
    public void testNoCallbacksAfterDeregistrationInUts()
    {
    	   
       		uts.registerTSListener ( listener );
       		uts.init ( uts.createTSInitInfo() );
       		assertFalse ( listener.isShutdownAfter() );
       		assertFalse ( listener.isShutdownBefore() );
       		assertTrue ( listener.isInitedBefore() );
       		assertTrue ( listener.isInitedAfter() );
       		
       		uts.removeTSListener ( listener );
       		
       		uts.shutdown(true);
       		assertFalse( listener.isShutdownAfter() );
       		assertFalse ( listener.isShutdownBefore() );
    }
    
    public void testNoCallbacksAfterDeregistrationInConfiguration()
    {
    	   
       		uts.registerTSListener ( listener );
       		uts.init ( uts.createTSInitInfo() );
       		assertFalse ( listener.isShutdownAfter() );
       		assertFalse ( listener.isShutdownBefore() );
       		assertTrue ( listener.isInitedBefore() );
       		assertTrue ( listener.isInitedAfter() );
       		
       		Configuration.removeTSListener ( listener );
       		
       		uts.shutdown(true);
       		assertFalse( listener.isShutdownAfter() );
       		assertFalse ( listener.isShutdownBefore() );
    }
    
    
    public void testInitCallbackOkWhenTmRunning()
    {
		
		uts.init ( uts.createTSInitInfo() );
		assertFalse ( listener.isShutdownAfter() );
		assertFalse ( listener.isShutdownBefore() );
		assertFalse ( listener.isInitedBefore() );
		assertFalse ( listener.isInitedAfter() );
		
		uts.registerTSListener ( listener );
		assertFalse ( listener.isInitedBefore() );
		assertTrue ( listener.isInitedAfter() );
		
		uts.shutdown(true);
		assertTrue ( listener.isShutdownAfter() );
		assertTrue ( listener.isShutdownBefore() );
    }
    
    public void testCallbacksWorkAfterRestart()
    {
    		uts.registerTSListener ( listener );
		uts.init ( uts.createTSInitInfo() );
		assertFalse ( listener.isShutdownAfter() );
		assertFalse ( listener.isShutdownBefore() );
		assertTrue ( listener.isInitedBefore() );
		assertTrue ( listener.isInitedAfter() );
		
		uts.shutdown(true);
		
		listener.reset();
		assertFalse ( listener.isInitedBefore() );
		assertFalse ( listener.isInitedAfter() );
		
		uts.init ( uts.createTSInitInfo() );
		
		assertTrue ( listener.isInitedBefore() );
		assertTrue ( listener.isInitedAfter() );
		uts.shutdown(true);
		
    }

}
