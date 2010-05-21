package com.atomikos.icatch.trmi;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.rmi.RemoteException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Stack;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;

import com.atomikos.diagnostics.Console;
import com.atomikos.icatch.CompositeCoordinator;
import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.system.Configuration;

/** 
 *Copyright &copy; 2001, Atomikos. All rights reserved.
 *
 *A Participant proxy multiplexes 2PC calls to a Participant server.
 *
 */

class ParticipantProxy 
implements Participant, Externalizable
{
    private Dictionary cascadelist_;
    private int globalsiblingcount_;
    private ParticipantServer server_;
    private String root_;
    private HeuristicMessage[] msgs_;
    private String name_;
    private boolean completed_;
    private String initialContextFactory_;
    private String providerUrl_;
    
    
    public ParticipantProxy () {}
    
    public ParticipantProxy ( String name , String initialContextFactory, 
				String providerUrl ,
				ParticipantServer server , Participant coord )
	{
		server_ = server;
		initialContextFactory_ = initialContextFactory;
		providerUrl_ = providerUrl;
		root_ = coord.getURI();
		name_ = name;
		//ADDED: immediately set msgs_ to 
		//reflect the tags for this CompositeCoordinator.
		//That way, heuristic hazards are not anonymous,
		//but rather the client TM can still get the EXACT
		//heuristic information!
		msgs_ = coord.getHeuristicMessages(); 
		//set completed to false; true asa commit/rollback return OK
		completed_ = false;		
	}

    public ParticipantProxy ( String name , String initialContextFactory, 
                              String providerUrl ,
                              ParticipantServer server , CompositeCoordinator coord )
    {
        server_ = server;
        initialContextFactory_ = initialContextFactory;
        providerUrl_ = providerUrl;
        root_ = coord.getCoordinatorId();
        name_ = name;
        //ADDED: immediately set msgs_ to 
        //reflect the tags for this CompositeCoordinator.
        //That way, heuristic hazards are not anonymous,
        //but rather the client TM can still get the EXACT
        //heuristic information!
        msgs_ = coord.getTags(); 
        //set completed to false; true asa commit/rollback return OK
        completed_ = false;
    }
    
    private void printMsg ( String msg , int level )
    {
    	Console console = Configuration.getConsole();
    	if ( console != null ) {
    		try {
    			console.println ( msg , level );
    		}
    		catch ( Exception ignore ) {}
    	}
    }
    
     /**
      *@see Participant
      */
      
    public boolean recover() 
    throws SysException
    {
         boolean ret = true;
         try {
            Hashtable props = new Hashtable();
            props.put ( 
                Context.INITIAL_CONTEXT_FACTORY , 
                initialContextFactory_ );
            props.put ( Context.PROVIDER_URL , providerUrl_ );
            Context ctx = new InitialContext ( props );
            //FIRST LOOKUP REF
            Object ref = ctx.lookup ( name_ );
            //ONLY THEN CAST! OTHERWISE, A NON_CAUGHT
            //EXCEPTION IS THROWN IF LOOKUP FAILS !!!!
            server_ = ( ParticipantServer )
                PortableRemoteObject.narrow ( ref , ParticipantServer.class );
        }
        catch ( Exception e ) {
            ret = false;
            printMsg ( "recover() failed for RMI participant proxy: " + 
            	toString() + " with error message: " + e.getMessage() , 
            	Console.INFO );
        }
        return ret;
    }
    
    public void writeExternal ( ObjectOutput out ) 
    throws IOException
    {
        out.writeObject ( root_ );
        //out.writeObject ( server_ );
        out.writeObject ( name_ );
        out.writeObject ( msgs_ );
        out.writeBoolean ( completed_ );
        out.writeObject ( providerUrl_ );
        out.writeObject ( initialContextFactory_ );
         
    }
    
    public void readExternal ( ObjectInput in ) 
    throws IOException, ClassNotFoundException
    { 
        root_ = ( String ) in.readObject ();
        //server_ = ( ParticipantServer ) in.readObject();
        name_ = ( String ) in.readObject();
        msgs_ = ( HeuristicMessage[] ) in.readObject();
        completed_ = in.readBoolean();
        providerUrl_ = ( String ) in.readObject();
        initialContextFactory_ = ( String ) in.readObject();
        recover();
    }


    /**
     *@see Participant.
     */

    public Object getState() 
    {
      return null;	
    }

    /**
     *@see Participant.
     */

    public void setCascadeList(java.util.Dictionary allParticipants)
        throws SysException
    {
        cascadelist_ = allParticipants;
    }

    /**
     *@see Participant.
     */

    public int getGlobalSiblingCount()
    {
        return globalsiblingcount_;
    }

    /**
     *@see Participant.
     */

    public void setGlobalSiblingCount(int count)
    {
        globalsiblingcount_ = count;
        
    }

  
    /**
     *@see Participant.
     */

    public int prepare()
        throws RollbackException,
	     HeurHazardException,
	     HeurMixedException,
	     SysException
    {
        int ret = -1;
        Stack errors = new Stack();
        if ( server_ == null ) recover();
        try {
	  ret = server_.prepare (root_ , globalsiblingcount_ , cascadelist_);
	 
        }
        catch (RemoteException re) {
	  errors.push ( re );
	  printMsg ( "prepare() failed for RMI participant proxy: " + 
					  toString() + " with error message: " + re.getMessage() , 
					  Console.INFO );
	  throw new SysException ( "Remote error in proxy: " + re.getMessage() , errors );
        }
        
		printMsg ( "prepare() OK for RMI participant proxy: " + 
						toString() , Console.INFO );
        
        return ret;
    }

    /**
     *@see Participant.
     */    

    public HeuristicMessage[] commit(boolean onePhase)
        throws HeurRollbackException,
	     HeurHazardException,
	     HeurMixedException,
             RollbackException,
	     SysException
    {
        //HeuristicMessage[] ret = null;
        Stack errors = new Stack();
        
		if ( server_ == null && !completed_ ) recover();
		
        try {  
	  //Try remote commit, ONLY if it did not return OK
	  //last time. If it returned OK, then the remote server
	  //has already forgotten the tx and there is no need
	  //to retry.
            if ( ! completed_ ) {
                if ( onePhase ) {
                    server_.commitOnePhase ( root_ , globalsiblingcount_ , cascadelist_ );
                }
                else server_.commit ( root_ );
            }
            completed_ = true;
        }
        catch ( RemoteException re ) {
	  errors.push ( re );
	  printMsg ( "commit ( " + onePhase + 
					  " ) failed for RMI participant proxy: " + 
					  toString() + " with error message: " + re.getMessage() , 
					  Console.INFO );
	  throw new SysException ("Error in proxy: " + re.getMessage() , errors );
        }
        //msgs_ = ret;
        //msgs_ REMOVED HERE: set immediately in constructor!
		printMsg ( "commit ( " + onePhase + " ) OK for RMI participant proxy: " + 
						toString() , Console.INFO );
        
        return msgs_;
    }


    /**
     *@see Participant.
     */
    
    public HeuristicMessage[] rollback()
        throws HeurCommitException,
	     HeurMixedException,
	     HeurHazardException,
	     SysException
    {
        //HeuristicMessage[] ret = null;
        Stack errors = new Stack();
	
		if ( server_ == null && ! completed_ ) recover();
		
        try {
	  if ( ! completed_ )
	      server_.rollback ( root_ );
	  completed_ = true;
        }
        catch ( RemoteException re ) {
	  errors.push ( re );
	  printMsg ( "rollback() failed for RMI participant proxy: " + 
					  toString() + " with error message: " + re.getMessage() , 
					  Console.INFO );
	  throw new SysException ( "Error in proxy: " + re.getMessage() , errors );
        }
        //msgs_ = ret;
        //msgs_ REMOVED HERE: set immediately in constructor!
		printMsg ( "rollback() OK for RMI participant proxy: " + 
						toString() , Console.INFO );
        return msgs_;
    }

    /**
     *@see Participant.
     */

    public void forget() 
    {
    	if ( server_ == null ) recover();
    	
        try {
	  server_.forget ( root_ );
        }
        catch (Exception re) {
	  //no problem here.
        }
		printMsg ( "forget() for RMI participant proxy: " + 
						toString() , Console.INFO );
    }
    
    public HeuristicMessage[] getHeuristicMessages()
    {
      return msgs_;	
    }


    /**
     *@see Participant.
     */

    public boolean equals(Object o)
    {
        if ( o==null ||  !(o instanceof ParticipantProxy) ) 
	  return false;
        
        ParticipantProxy p = ( ParticipantProxy ) o;
        return ( ( p.root_.equals ( root_ ) ) && 
	       ( p.name_.equals ( name_ ) ) );
     
    }

    /**
     *@see Participant.
     */
    public int hashCode() 
    {
        return root_.toString().hashCode() + 
                  name_.hashCode();
    }
    
    /**
     *Returns a string representation of this participant proxy.
     */
    
    public String toString() 
    {
        return name_ + " : " + root_.toString();	
    }

    
    public String getURI()
    {
        return providerUrl_ + "/" + name_ + "?" + root_;
    }

}
