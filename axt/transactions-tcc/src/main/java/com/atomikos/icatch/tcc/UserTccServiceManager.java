package com.atomikos.icatch.tcc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.TSListener;
import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.atomikos.icatch.system.Configuration;
import com.atomikos.tcc.TccService;
import com.atomikos.tcc.TccServiceManager;

public class UserTccServiceManager 
implements TccServiceManager
{
	
	private static final Map idToParticipantMap = new HashMap();
	//tracks id to participant instances for active work
	
	private static void addToMap ( String id , TccParticipant participant )
	{
		synchronized ( idToParticipantMap ) {
			idToParticipantMap.put ( id , participant );
		}
	}
	
	private static TccParticipant removeFromMap ( String id )
	{
		TccParticipant ret = null;
		synchronized ( idToParticipantMap ) {
			ret = ( TccParticipant ) idToParticipantMap.remove ( id );
		}
		return ret;
	}
	

	//static TccService application;
	
	static ArrayList services = new ArrayList();
	
	/**
	 * Gets the TccServices instances in use. 
	 * This method can be used by the transaction service 
	 * for recovery purposes.
	 * 
	 * @return The application-level instances in an iterator.
	 */
	static synchronized Iterator getTccServices()
	{
		List clone = ( List ) services.clone();
		return clone.iterator();
	}

	/**
	 * Initializes the registry with an application-level
	 * service implementation. This method must be called
	 * before any other, to allow application-level recovery.
	 * Preferably, this method should be called at 
	 * startup time of the application (server).
	 * 
	 * @param app
	 */
	protected static synchronized void doRegisterForRecovery ( TccService app )
	{
		if ( app == null ) throw new IllegalArgumentException ( "Null not allowed");
		boolean reregistration = 
			Configuration.getResource ( app.toString() ) != null;
		//if already registered: remove and replace by new 
		//needed to allow restart and refresh of webapp
		if ( reregistration ) services.remove ( app );
		services.add ( app );
		if ( ! reregistration ) {
			String resourceName = app.toString();
			TccResource resource = 
				new TccResource ( resourceName );
			Configuration.addResource ( resource );
			
			//DISABLED: RESOURCE NEEDED ONLY TO TRIGGER
			//RECOVERY UPON REGISTRATION!
			//add a listener to allow (re)start of TM
			//(adds resource again if needed)
			//TccTSListener listener = new TccTSListener ( app );
			//Configuration.addTSListener ( listener );		
		}		
	}
	
	/**
	 * Deregisters a previously registered application TccService. 
	 * This method can be called when the application becomes temporarily
	 * unavailable (e.g., during restart by the container - if any).
	 * Note, however, that this will effectively render application-level
	 * recovery and termination callbacks impossible until the application
	 * is re-registered.
	 * 
	 * @param app
	 */
	protected static synchronized void doDeregister ( TccService app )
	{
		if ( app == null ) return;
		services.remove ( app );
		Configuration.removeResource ( app.toString() );
		
	}
	
	//
	// INSTANCE VARIABLES BELOW
	//
	
	private CompositeTransactionManager ctm;
	private boolean autoStartup;
	
	private void checkInit() throws IllegalStateException
	{
		//if ( application == null ) throw new IllegalStateException ( "No TccService found?" );
		ctm = Configuration.getCompositeTransactionManager();
		if ( ctm == null ) {
			if ( ! autoStartup )
				throw new IllegalStateException( "Transaction service not running?" );
			else {
				synchronized ( UserTccServiceManager.class ) {
					UserTransactionService uts = new UserTransactionServiceImp ();
	                 TSInitInfo info = uts.createTSInitInfo ();
	                 uts.init ( info );
				}
			}
		}
		
	}
	
	/**
	 * Sets the autoStartup mode. If true then the transaction
	 * service will be started if it is not already running.
	 * Defaults to false.
	 * 
	 * @param autoStartup
	 */
	public void setAutoStartup ( boolean autoStartup )
	{
		Configuration.logInfo ( "TCC: setAutoStartup ( " + autoStartup + " )" );
		this.autoStartup = autoStartup;
	}
	
	public String register ( TccService service , long timeout ) 
	{
		Configuration.logInfo ( "TCC: register ( " + service + " , " + timeout + " )" );
		checkInit();
		String ret = null;
		CompositeTransaction ct = ctm.createCompositeTransaction ( timeout );
		ct.getCompositeCoordinator().setRecoverableWhileActive();
		ret = ct.getTid();
		if ( service != null ) {
			//register a participant with the new tx
			TccParticipant p = new TccParticipant ( service , ret , timeout );
			addToMap ( ret , p );
			ct.addParticipant ( p );
		}
		Configuration.logInfo ( "TCC: register returning id: " + ret );
		return ret;
	}

	public void completed ( String id ) 
	{
		Configuration.logInfo ( "TCC: completed ( " + id + " )" );
		checkInit();
		final CompositeTransaction ct = ctm.getCompositeTransaction ( id );
		
		//make sure participant knows we're completed
		//so pending timeout threads can cancel
		TccParticipant p = removeFromMap ( id );
		if ( p != null ) {
			p.setCompleted();
		}
		
		terminateTransaction ( ct , true );
		
//		if ( ct == null ) {
//			//transaction has been terminated already or so?
//			//note:this can't be due to timeout because timeout
//			//does not remove from CTM!!!
//			throw new IllegalStateException ( "Transaction " + id + " is not active in this thread" );
//		}
////		try {
////			ct.commit();
////	
////		} catch (HeurRollbackException e) {
////			throw new HeurMixedException ( e.getHeuristicMessages() );
////		} catch (HeurHazardException e) {
////			throw new HeurMixedException ( e.getHeuristicMessages() );
////		} 
//		
//		new Thread() {
//			
//			public void run () {
//				try {
//					ct.commit();
//				}
//				catch ( Exception ignore ) {
//					//outcome is given by app-level
//					//cancel/confirm
//				}
//			}
//			
//		}.start();

	}
	
	/**
	 * Terminates the given transaction in a separate thread.
	 * This method also restores the tread association of the
	 * parent (if any).
	 * @param tx The transaction.
	 * @param completed True if OK, false if failed.
	 */
	private void terminateTransaction ( CompositeTransaction tx , boolean completed )
	{
		final CompositeTransaction ct = tx;
		
		if ( ct == null ) {
			//transaction has been terminated already or so?
			//note:this can't be due to timeout because timeout
			//does not remove from CTM!!!
			throw new IllegalStateException ( "Transaction " + ct + " is null?" );
		}
		
		
		//termination of root is done in a different thread to avoid recursive calls
		//into the TCC service implemenation (completed/failed -> confirm etcetera)
		NonReentrantTerminationThread thread = new NonReentrantTerminationThread ( ct , completed );
		if ( ! ct.isRoot() ) {
			thread.run();
		}
		else {
			thread.start();
			thread.waitUntilDone();
			//commit or rollback info is known in the TCC service
			//so don't throw any errors here
		}
		
	}

	public void failed ( String id ) 
	{
		Configuration.logInfo ( "TCC: failed ( " + id + " )" );
		checkInit();
		final CompositeTransaction ct = ctm.getCompositeTransaction ( id );
		
		//make sure participant knows we're completed
		//so pending timeout threads can cancel
		TccParticipant p = removeFromMap ( id );
		if ( p != null ) {
			//System.out.println ( "Completing: " + id );
			p.setCompleted();
		}
		
		terminateTransaction ( ct , false );
		
//		if ( ct != null ) {
//			new Thread() {
//				
//				public void run () {
//					try {
//						ct.rollback();
//					}
//					catch ( Exception ignore ) {
//						//outcome is given by app-level
//						//cancel/confirm
//					}
//				}
//				
//			}.start();
//		}

	}

	public void suspend ( String id )
	{
		Configuration.logInfo ( "TCC: suspend ( " + id + " )" );
		checkInit();
		CompositeTransaction ct = ctm.getCompositeTransaction();
		//ct can't be null due to timeout, because timeout does not remove from CTM
		if ( ct != null && ct.getTid().equals ( id ) ) ctm.suspend();
		else throw new IllegalStateException ( "Transaction " + id + " is not active in this thread" );

	}

	public void resume ( String id )
	{
		Configuration.logInfo ( "TCC: resume ( " + id + " )" );
		checkInit();
		CompositeTransaction ct = ctm.getCompositeTransaction ( id );
		//ct can't be null due to timeout, because timeout does not remove from CTM
		if ( ct != null ) ctm.resume ( ct );
		else throw new IllegalStateException ( "Transaction not found: " + id );

	}

	
	private static class TccTSListener 
	implements TSListener
	{
		
		private TccService application;
		
		public TccTSListener ( TccService application )
		{
			this.application = application;
		}
		
		public void init (
				boolean before, Properties properties ) 
		{
			if ( application == null ) return;
			//we can't do anything if application is not set
			
			String resourceName = application.toString();
			if ( before && 
					Configuration.getResource ( resourceName ) == null ) {	
				
				TccResource resource = 
					new TccResource ( resourceName );
				Configuration.addResource ( resource );
			}
			
		}

		public void shutdown ( boolean before ) 
		{
			
		}
	}


	public void registerForRecovery(TccService service) {
		doRegisterForRecovery ( service );
		
	}

	public void deregisterForRecovery(TccService service) {
		doDeregister ( service );
		
	}

}
