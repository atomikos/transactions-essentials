package com.atomikos.icatch.imp;

import java.util.Dictionary;
import java.util.Properties;

import com.atomikos.finitestates.FSMEnterEvent;
import com.atomikos.finitestates.FSMEnterListener;
import com.atomikos.finitestates.FSMPreEnterListener;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.TestSynchronization;
import com.atomikos.icatch.TxState;
import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;
import com.atomikos.util.TestCaseWithTemporaryOutputFolder;

public class DeadlockTestJUnit extends TestCaseWithTemporaryOutputFolder {

	private UserTransactionServiceImp uts;
	private CompositeTransactionManager ctm;
	private CompositeTransaction ct;
	private CoordinatorImp coordinator;
	
	public DeadlockTestJUnit ( String name )
	{
		super ( name );
	}

	protected void setUp() 
	{
		super.setUp();
        uts =
            new UserTransactionServiceImp();
        
        TSInitInfo info = uts.createTSInitInfo();
        Properties properties = info.getProperties();        
        properties.setProperty ( 
				AbstractUserTransactionServiceFactory.TM_UNIQUE_NAME_PROPERTY_NAME , "UserTransactionTestJUnit" );
        properties.setProperty ( AbstractUserTransactionServiceFactory.OUTPUT_DIR_PROPERTY_NAME , getTemporaryOutputDir() );
        properties.setProperty ( AbstractUserTransactionServiceFactory.LOG_BASE_DIR_PROPERTY_NAME , getTemporaryOutputDir()
        	        );
        properties.setProperty ( AbstractUserTransactionServiceFactory.CONSOLE_LOG_LEVEL_PROPERTY_NAME , "DEBUG" );
        properties.setProperty ( AbstractUserTransactionServiceFactory.MAX_ACTIVES_PROPERTY_NAME , "25000" );
        uts.init ( info );
        ctm = uts.getCompositeTransactionManager();
        ct = ctm.createCompositeTransaction ( 5000 );
        coordinator = ( CoordinatorImp ) ct.getCompositeCoordinator();
	}
	
    protected void tearDown()
    {
    
        uts.shutdown ( true );
        super.tearDown();
        
    }
    
    public void testDeadlockOnRecursive2PC() throws Exception
    {
    	RecursiveParticipant p = new RecursiveParticipant ( coordinator );
    	ct.addParticipant(p);
    	ct.addParticipant ( new ReadOnlyParticipant ( coordinator ) );
    	try {
    		ct.commit();
    		fail ( "Recursive 2PC works - not allowed?" );
    	}
    	catch ( RollbackException ok ) {}
    }
    
    public void testIssue21705() throws Exception 
    {
    	FSMEnterListener l = new FSMEnterListener () {
			public void entered(FSMEnterEvent e) throws IllegalStateException {
					try {
						Thread.sleep ( 1000 );
					} catch (InterruptedException e1) {						
						e1.printStackTrace();
					}
					(( CompositeTransactionImp ) ct).entered ( null );
			}
    	};
    	
    	coordinator.addFSMEnterListener( 
    			l,
    			TxState.ABORTING );
    	
    	final DeadlockThread timeoutThread = new DeadlockThread() {

			protected void doRun() throws Exception {
				coordinator.rollback();
				//System.out.println ( "Coordinator set to state " + coordinator.getState() );
			}
    		
    	};
    	
    	timeoutThread.start();
    	
    	DeadlockThread registrationThread = new DeadlockThread() {

			protected void doRun() throws Exception {
				timeoutThread.waitUntilStarted();
				coordinator.registerSynchronization(new TestSynchronization() );
			}
    		
    	};
    	
    	registrationThread.start();
    	//System.out.println ( "registration thread started" );
    	registrationThread.assertIsDone();
    }
    
    
    public void testIssue21806() throws Exception
    {
 
    	//simulate concurrent timeout/rollback and registerSynchronization by app thread
    	
    	FSMPreEnterListener l = new FSMPreEnterListener () {
			public void preEnter(FSMEnterEvent e) throws IllegalStateException {
					//System.out.println ( "preEnter" );
					try {
						Thread.sleep ( 1000 );
					} catch (InterruptedException e1) {
						
						e1.printStackTrace();
					}
					coordinator.getObjectImage();
			}
    		
    	};
    	
    	
    	coordinator.addFSMPreEnterListener( 
    			l,
    			TxState.ABORTING );
    	
    	final DeadlockThread timeoutThread = new DeadlockThread() {

			protected void doRun() throws Exception {
				coordinator.rollback();
				//System.out.println ( "Coordinator set to state " + coordinator.getState() );
			}
    		
    	};
    	
    	timeoutThread.start();
    	
    	DeadlockThread registrationThread = new DeadlockThread() {

			protected void doRun() throws Exception {
				timeoutThread.waitUntilStarted();
				try {
				coordinator.registerSynchronization(new TestSynchronization() );
				} catch ( IllegalStateException ok ) {}
			}
    		
    	};
    	
    	registrationThread.start();
    	//System.out.println ( "registration thread started" );
    	registrationThread.assertIsDone();
    	
    }
    
//    public void testForceShutdown()
//    {
//    	//dummy test to force VM exit - or ant test blocks!
//    	System.exit(0);
//    }
    
    
    static abstract class DeadlockThread extends Thread
    {
    	private boolean isDone;
    	private boolean started;

    	
    	
    	private synchronized void setStarted() 
    	{
    		started = true;
    		notifyAll();
    	}
    	
    	public void run() {
    		isDone = false;
    		setStarted();
    		try {
    			doRun();
    		}
    		catch ( Exception e ) {
    			e.printStackTrace();
    		}
    		finally {
    			isDone = true;
    		}
    		
    	}
    	
    	public void waitUntilStarted()
    	{
    		try {
    		synchronized ( this ) {
    		while ( !started )
				
					wait();
    		}
			//wait extra time to allow started thread to execute
			//and execute its dangerous code
			Thread.sleep ( 500 );

			} catch (InterruptedException e) {
					e.printStackTrace();
			}
    	}
    	
    	public void assertIsDone() {
    		try {
    			//System.out.println ( "waiting for done" );
				Thread.sleep ( 10000 );
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//System.out.println ( "wait finished" );
    		if ( ! isDone ) throw new IllegalStateException ( "Deadlock?" );
    		//else System.out.println ( "isDone=" + isDone );
    	}
    	
    	protected abstract void doRun() throws Exception;
    }
    
    static class RecursiveParticipant implements Participant 
    {
    	private transient CoordinatorImp c;
    	
    	RecursiveParticipant ( CoordinatorImp c ) 
    	{
    		this.c = c;
    	}

		public HeuristicMessage[] commit(final boolean onePhase)
				throws HeurRollbackException, HeurHazardException,
				HeurMixedException, RollbackException, SysException {
			
			DeadlockThread t = new DeadlockThread() {

				protected void doRun() throws Exception {
					c.commit(onePhase);
				}
				
			};
			t.start();
			t.assertIsDone();
			return null;
		}

		public void forget() {
			
			
		}

		public HeuristicMessage[] getHeuristicMessages() {
			
			return null;
		}

		public String getURI() {
			
			return null;
		}

		public int prepare() throws RollbackException, HeurHazardException,
				HeurMixedException, SysException {
			DeadlockThread t = new DeadlockThread() {

				protected void doRun() throws Exception {
					c.prepare();
				}
				
			};
			t.start();
			t.assertIsDone();
			throw new RollbackException();
		}

		public boolean recover() throws SysException {
			
			return false;
		}

		public HeuristicMessage[] rollback() throws HeurCommitException,
				HeurMixedException, HeurHazardException, SysException {
			
			DeadlockThread t = new DeadlockThread() {

				protected void doRun() throws Exception {
					c.rollback();
				}
				
			};
			t.start();
			t.assertIsDone();
			return null;
		}

		public void setCascadeList(Dictionary allParticipants)
				throws SysException {
			
			
		}

		public void setGlobalSiblingCount(int count) {
			
			
		}
    	
    }
}
