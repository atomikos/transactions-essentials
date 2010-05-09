package com.atomikos.icatch.tcc;

import java.io.Serializable;
import java.util.Dictionary;
import java.util.Iterator;

import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.StringHeuristicMessage;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.system.Configuration;
import com.atomikos.tcc.TccException;
import com.atomikos.tcc.TccService;

class TccParticipant 
implements Participant, Serializable 
{
	

	private static final long serialVersionUID = 5462322704503076842L;
	
	private transient TccService application;
	//the app-level callback interface, restored
	//on recovery
	
	private String id;
	//the correlation id to use for application callbacks
	
	private boolean completed;
	//true as soon as cancel/confirm should work; otherwise wait
	
	private long timeout;
	//the timeout to wait for completed
	
	private HeuristicMessage[] hmsgs;

	
	TccParticipant ( TccService application , String id , long timeout )
	{
		this.id = id;
		this.application = application;
		this.timeout = timeout;
		completed = false;
	}

	public boolean recover() throws SysException 
	{
		boolean recovered = false;
		if ( application == null ) {
			//a deserialized instance!
			Configuration.logDebug ( "TCC: recovery looking for application..." );
			Iterator it = UserTccServiceManager.getTccServices();
			while ( it.hasNext() && application == null ) {
				TccService next = ( TccService ) it.next();
				if ( next.recover ( id ) ) application = next;
			}
			//since we were deserialized, completed must be true to allow cancel
			//but ONLY IF application was found! otherwise, wait until recover
			//is triggered again by addition of the TccResource
			if ( application != null ) {
				recovered = true;
				setCompleted();
				Configuration.logDebug ( "TCC: recovery found application" );
			}
			else {
				Configuration.logWarning ( "TCC: recovery could not find application" );
			}
		}
		else recovered = true;
		return recovered;
	}
	
	synchronized void setCompleted()
	{	
		this.completed = true;
		notifyAll();
		Configuration.logDebug ( "TCC: comletion of work with id " + id );
	}
	
	boolean isCompleted()
	{
		return this.completed;
	}

	public String getURI() 
	{
		return id;
	}

	public void setCascadeList ( Dictionary allParticipants )  
	throws SysException 
	{
		//ignore, not relevant

	}

	public void setGlobalSiblingCount ( int count ) 
	{
		//ignore, not relevant

	}

	public int prepare() 
	throws RollbackException, HeurHazardException,
			HeurMixedException, SysException 
	{
		Configuration.logDebug ( "TCC: preparing work with id " + id );
		if ( ! isCompleted() ) throw new RollbackException();
		
		//we're early prepared and never read only
		return Participant.READ_ONLY + 1;
	}

	public HeuristicMessage[] commit ( boolean onePhase )
			throws HeurRollbackException, HeurHazardException,
			HeurMixedException, RollbackException, SysException 
	{
		Configuration.logDebug ("TCC: committing work with id " + id );
		HeuristicMessage[] msgs = getHeuristicMessages();
		if ( application == null ) {
			//application not yet started?
			Configuration.logWarning ( "TCC: commit of " + id + " without application!" );
			throw new HeurHazardException ( msgs );
		}
		if ( ! isCompleted() ) {
			Configuration.logWarning ( "TCC: commit attempted of incomplete work with id " + id );
			throw new HeurHazardException ( msgs );
		}
		
		try {
			application.confirm ( id );
		} catch ( TccException e ) {
			throw new HeurHazardException ( msgs );
		}
		Configuration.logDebug ( "TCC: commit done of id " + id );
		
		return msgs;
	}

	public synchronized HeuristicMessage[] rollback() 
	throws HeurCommitException,
			HeurMixedException, 
			HeurHazardException, SysException 
	{
	
		Configuration.logDebug ( "TCC: about to rollback work with id " + id );
		if ( ! isCompleted() ) {
			//wait until completed, to avoid that we lose
			//cancel events for uncompleted (but persisted) work!!!
			//this will also wait during recovery, if timeout
			//happens before the application is available
			try {
				Configuration.logDebug ( "TCC: about to wait for completion of work with id " + id );
				//wait (again) if needed, but at most timeout ms
				wait ( timeout );
				Configuration.logDebug ( "TCC: done waiting for completion of work with id " + id );
			}
			catch ( InterruptedException e ){}
		}
		
		
		HeuristicMessage[] msgs = getHeuristicMessages();
		if ( application == null || ! isCompleted() ) {
			Configuration.logWarning ( "TCC: detected hazard situation for work with id " + id );
			//application not yet started, or completion is 
			//taking way too long
			//Since we don't want to wait forever, we terminate heuristically. Indeed, the following
			//is happening:
			//1. timeout during active cancels the activity
			//2. the cancel signal is not propagated because of the active application  (and is lost)
			//3. IF the transaction were allowed to terminate silently then it will be removed from the logs, so
			//4. when the application does finish (after preliminary commit) then who will tell it to cancel?
			//This is particularly nasty with a crash between 3 and 4, so therefore we terminate heuristically
			//to keep the transaction in the logs for manual intervention.
			throw new HeurHazardException ( msgs );
		}
		
		try {
			//System.out.println ("canceling: " + id );
			application.cancel ( id );
		} catch ( TccException e ) {
			throw new HeurHazardException ( msgs );
		}
		
		Configuration.logDebug ( "TCC: done rollback of work with id " + id );
		
		return msgs;
	}

	public void forget() 
	{
		//nothing to do

	}

	public HeuristicMessage[] getHeuristicMessages() 
	{
		if ( hmsgs == null ) {
			String msg = null;
//			if ( application != null ) {
//				msg = application.getDescription ( id );
//			}
			if ( msg == null ) msg = "TCC Service ID: " + id;
			hmsgs = new HeuristicMessage[1];
			hmsgs[0] = new StringHeuristicMessage ( msg );
		}
		return hmsgs;
		
	}

}
