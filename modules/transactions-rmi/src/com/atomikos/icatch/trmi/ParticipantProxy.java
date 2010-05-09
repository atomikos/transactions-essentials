//$Id: ParticipantProxy.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//$Log: ParticipantProxy.java,v $
//Revision 1.1.1.1  2006/10/02 15:21:17  guy
//Import into CVS.
//
//Revision 1.1.1.1  2006/04/29 08:55:45  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:38  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:33  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:47:01  guy
//Import.
//
//Revision 1.2  2006/03/21 13:24:01  guy
//Introduced active recovery and CompTx properties as meta-tags.
//
//Revision 1.1.1.1  2006/03/09 14:59:32  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.17  2005/08/09 15:24:13  guy
//Updated javadoc, and redesigned CompositeTransaction interface
//(eliminated TransactionControl and CompositeTerminator).
//
//Revision 1.16  2005/08/05 15:04:23  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.15  2004/11/24 10:20:18  guy
//Updated error messages.
//
//Revision 1.14  2004/10/12 13:03:53  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.13  2004/09/09 15:00:54  guy
//Added recover() on 2PC also.
//
//Revision 1.12  2004/09/09 13:10:28  guy
//Regenerated stubs (marshal errros).
//Corrected bug in ParticipantProxy: lookup and cast in one go
//seemed to throw non-caught exception during recover()?
//
//Revision 1.11  2004/09/01 13:39:23  guy
//Merged changes from TransactionsRMI 1.22.
//
//Revision 1.10  2004/03/22 15:38:14  guy
//Merged-in changes from branch redesign-4-2003.
//$Id: ParticipantProxy.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//$Log: ParticipantProxy.java,v $
//Revision 1.1.1.1  2006/10/02 15:21:17  guy
//Import into CVS.
//
//Revision 1.1.1.1  2006/04/29 08:55:45  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:38  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:33  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:47:01  guy
//Import.
//
//Revision 1.2  2006/03/21 13:24:01  guy
//Introduced active recovery and CompTx properties as meta-tags.
//
//Revision 1.1.1.1  2006/03/09 14:59:32  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.17  2005/08/09 15:24:13  guy
//Updated javadoc, and redesigned CompositeTransaction interface
//(eliminated TransactionControl and CompositeTerminator).
//
//Revision 1.16  2005/08/05 15:04:23  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.15  2004/11/24 10:20:18  guy
//Updated error messages.
//
//Revision 1.14  2004/10/12 13:03:53  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.13  2004/09/09 15:00:54  guy
//Added recover() on 2PC also.
//
//Revision 1.12  2004/09/09 13:10:28  guy
//Regenerated stubs (marshal errros).
//Corrected bug in ParticipantProxy: lookup and cast in one go
//seemed to throw non-caught exception during recover()?
//
//Revision 1.11  2004/09/01 13:39:23  guy
//Merged changes from TransactionsRMI 1.22.
//
//Revision 1.9.2.1  2004/04/30 14:33:06  guy
//Included different log levels, and added immediate rollback for extent
//participants.
//
//Revision 1.9  2003/08/27 06:24:07  guy
//Adapted to RMI-IIOP.
//$Id: ParticipantProxy.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//$Log: ParticipantProxy.java,v $
//Revision 1.1.1.1  2006/10/02 15:21:17  guy
//Import into CVS.
//
//Revision 1.1.1.1  2006/04/29 08:55:45  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:38  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:33  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:47:01  guy
//Import.
//
//Revision 1.2  2006/03/21 13:24:01  guy
//Introduced active recovery and CompTx properties as meta-tags.
//
//Revision 1.1.1.1  2006/03/09 14:59:32  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.17  2005/08/09 15:24:13  guy
//Updated javadoc, and redesigned CompositeTransaction interface
//(eliminated TransactionControl and CompositeTerminator).
//
//Revision 1.16  2005/08/05 15:04:23  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.15  2004/11/24 10:20:18  guy
//Updated error messages.
//
//Revision 1.14  2004/10/12 13:03:53  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.13  2004/09/09 15:00:54  guy
//Added recover() on 2PC also.
//
//Revision 1.12  2004/09/09 13:10:28  guy
//Regenerated stubs (marshal errros).
//Corrected bug in ParticipantProxy: lookup and cast in one go
//seemed to throw non-caught exception during recover()?
//
//Revision 1.11  2004/09/01 13:39:23  guy
//Merged changes from TransactionsRMI 1.22.
//
//$Id: ParticipantProxy.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Revision 1.10  2004/03/22 15:38:14  guy
//$Id: ParticipantProxy.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Merged-in changes from branch redesign-4-2003.
//$Id: ParticipantProxy.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//
//$Id: ParticipantProxy.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Revision 1.8.2.1  2003/06/20 16:31:53  guy
//$Id: ParticipantProxy.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//*** empty log message ***
//$Id: ParticipantProxy.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//
//$Id: ParticipantProxy.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Revision 1.8  2003/03/23 15:20:04  guy
//$Id: ParticipantProxy.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Corrected BUG in 1PC requested from remote TM in RMI. Added tests for this.
//$Id: ParticipantProxy.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//
//$Id: ParticipantProxy.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Revision 1.7  2003/03/11 06:39:16  guy
//$Id: ParticipantProxy.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Merged in changes from transactionsJTA100 branch.
//$Id: ParticipantProxy.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//
//$Id: ParticipantProxy.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Revision 1.8  2003/03/23 15:20:04  guy
//$Id: ParticipantProxy.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Corrected BUG in 1PC requested from remote TM in RMI. Added tests for this.
//$Id: ParticipantProxy.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//
//$Id: ParticipantProxy.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Revision 1.7  2003/03/11 06:39:16  guy
//$Id: ParticipantProxy.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Merged in changes from transactionsJTA100 branch.
//$Id: ParticipantProxy.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//
//Revision 1.6.4.2  2003/01/29 17:20:07  guy
//Adapted to use JNDI binding instead of Naming of RMI.
//
//Revision 1.6.4.1  2002/11/18 18:52:27  guy
//Corrected BUG: participant proxy did not restore server_ after readin.
//UserTransaction now supports serial JTA txs by default.
//
//Revision 1.6  2002/03/01 10:48:08  guy
//Updated to new prepare exception of HeurMixed.
//
//Revision 1.5  2002/02/14 10:12:12  guy
//Changed commit/rollback to go remote ONLY IF previous attempt was NOT ok.
//Needed to support consistent replies on multiple commit calls (recovery).
//
//Revision 1.4  2002/02/12 15:02:24  guy
//Adapted for recover() method in Participant interface.
//
//Revision 1.3  2002/01/10 11:03:10  guy
//Corrected writeExternal bug in ParticipantProxy and adapted UserTransactionImp to use the new LogAdministrators.
//
//Revision 1.2  2001/10/29 16:38:11  guy
//Changed UniqueId for String.
//
//Revision 1.1.1.1  2001/10/09 12:37:26  guy
//Core module
//
//Revision 1.2  2001/03/23 17:00:37  pardon
//Lots of implementations for Terminator and proxies.
//
//Revision 1.1  2001/03/21 17:26:51  pardon
//Added proxies and server interfaces / classes.
//

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
