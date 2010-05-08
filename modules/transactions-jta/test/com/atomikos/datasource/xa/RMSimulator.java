package com.atomikos.datasource.xa;

import java.util.Hashtable;
import java.util.Vector;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

/**
*
*
*A class that simulates an XAResource for testing.
*This class can be useful in all situations where XAResource functionality
*has to be simulated.
*/

public class RMSimulator implements XAResource {
    
    public static Object RM_SUSPENDED=new Integer(0);
    public static Object RM_PREPARED=new Integer(1);
    public static Object RM_ABORTED=new Integer(2);
    public static Object RM_COMMITTED=new Integer(4);
    public static Object RM_UNKNOWN=new Integer(5);
    public static Object RM_ACTIVE=new Integer(6);
    public static Object RM_FAILED=new Integer(7);
    public static Object RM_DONE=new Integer(8);
    public static Object RM_READONLY=new Integer(9);
    public static Object RM_HEURABORT= new Integer(10);
    public static Object RM_HEURCOMMIT= new Integer(11);
    
    private Hashtable txStates=new Hashtable();
    private Vector recoverlist = new Vector();
    private int timeout=100;
    private Xid lastXid_ = null;
    
    private Xid getLastXid()
    {
        return lastXid_; 
    }
    
    private void setLastXid ( Xid xid )
    {
        lastXid_ = xid; 
    }
    
     /**
      *@see XAResource
      */
      
    public void start(Xid xid, int flags) throws XAException{

        if (flags!=XAResource.TMJOIN && flags!=XAResource.TMRESUME) {

	  if (txStates.containsKey(xid)) 
	      throw new XAException(XAException.XAER_DUPID);	
        }//if
        
        txStates.put(xid,RM_ACTIVE);
        setLastXid ( xid );
    }
    
    /**
     *Get the current state of the given xid.
     *
     *@param xid The xid to check.
     *@return Object The state object.
     */
     
    public Object getState(Xid xid) {

        return txStates.get(xid);
    }
    
     /**
      *Get the current state of the <b>last</b> xid 
      *that was seen by this resource.
      *
      *@return Object The state of the last xid, or null if no
      *last xid is known.
      */
      
    public Object getState()
    {
        Object ret = null;
        
        if ( getLastXid() != null )
            ret = txStates.get ( getLastXid() );
            
        return ret;
    }
    
    /**
      *@see XAResource
      */
      
    public void end(Xid xid, int flags) throws XAException{

        if (!txStates.containsKey(xid)) 
	  throw new XAException(XAException.XAER_NOTA);

        Object state=txStates.get(xid);

        if (state.equals(RM_ABORTED)) 
	  throw new XAException(XAException.XA_RBROLLBACK);

        switch (flags) {

        case XAResource.TMFAIL: txStates.put(xid,RM_FAILED);break;
        case XAResource.TMSUSPEND: txStates.put(xid,RM_SUSPENDED);break;
        case XAResource.TMSUCCESS: txStates.put(xid,RM_DONE);break;
        default: throw new XAException();

        }//switch
    }
    
     /**
      *@see XAResource
      */
      
    public void commit(Xid xid, boolean onePhase) throws XAException{

		Object state=txStates.get(xid);

		if (state!=null && 
		    ( state.equals(RM_ABORTED) ||
		      state.equals (RM_HEURABORT ))) {
		    txStates.remove(xid);

		    if (onePhase) 
		        throw new XAException(XAException.XA_RBROLLBACK);
		    else 
		        throw new XAException(XAException.XA_HEURRB);
		}
		if (state==null || 
		    (onePhase && !state.equals(RM_DONE)) || 
		    (!onePhase && !state.equals(RM_PREPARED))) 
		    throw new XAException(XAException.XAER_PROTO);

		txStates.remove(xid);
		recoverlist.remove ( xid );
		//txStates.put(xid,RM_COMMITTED);
    }
    
     /**
      *Simulate read-only behaviour for the last xid.
      */
      
    public void setReadOnly()
    {
        if ( getLastXid() != null )
            txStates.put ( getLastXid() , RM_READONLY ); 
    }
    
    /**
     *Setter method for simulating read-only behaviour.
     *@param xid The xid to simulate read-only for.
     */

    public void setReadOnly ( Xid xid ) {

        txStates.put(xid,RM_READONLY);
    }
    
     /**
      *Simuate rolled-back behaviour for the last xid.
      *
      *@return boolean True if successful and prepared.
      *In that case, a heuristic rollback will result on commit.
      */
      
    public boolean setRolledBack()
    {
        boolean ret = false;
        if ( getLastXid() != null )
            ret = setRolledBack ( getLastXid() ); 
        return ret;
    }
    
    /**
     *Setter method for simulating rolled-back behaviour.
     *@param xid The xid to simulate rollback for.
     *If not prepared, then a timeout rollback is simulated.
     *If prepared, a heuristic abort is simulated.
     *@return boolean True iff prepared.
     */
     
    public boolean  setRolledBack(Xid xid){
        boolean ret = false;
        Object state=txStates.get(xid);

        if (state!=null && state.equals(RM_PREPARED)) {
	  txStates.put(xid,RM_HEURABORT);
	  ret = true;
        }
        else {
        	  
	  txStates.put(xid,RM_ABORTED);
	  ret = false;
        }
        
        return ret;
    }
    
     /**
      *Simulate heuristic commit for the last xid.
      */
      
    public void setHeuristicallyCommitted()
    {
        if ( getLastXid() != null )
            setHeuristicallyCommitted ( getLastXid() ); 
    }
    
    /**
     *Setter for simulating heuristic commit.
     *@param xid The transaction that is to behave
     *as if heuristically committed. Only has effect
     *if the transaction is PREPARED already!
     */
     
    public void setHeuristicallyCommitted(Xid xid) {

        Object state=txStates.get(xid);

        if (state!=null && state.equals(RM_PREPARED)) 
	  txStates.put(xid,RM_HEURCOMMIT);
    }
    
    /**
     *@see XAResource
     */
     
    public int prepare(Xid xid) throws XAException {
        
        Object state=null;
        state=txStates.get(xid);

        if (state==null ) 
	  throw new XAException(XAException.XAER_NOTA);

        if (state.equals(RM_READONLY)) {
	  
	  txStates.remove(xid);
	  return XAResource.XA_RDONLY;
        }//if

        else if (state.equals(RM_ABORTED)) {
	  
	  txStates.remove(xid);
	  throw new XAException(XAException.XA_RBROLLBACK);
        }//else if

        else {
	  
	  if (!state.equals(RM_DONE)) 
	      throw new XAException(XAException.XAER_PROTO);
	  
	  recoverlist.addElement ( xid );
	  txStates.put(xid,RM_PREPARED);
	 
	  return XAResource.XA_OK;
        }//else
    }
    
    /**
     *@see XAResource
     */
     
    public void rollback(Xid xid) throws XAException{

        Object state=txStates.get(xid);
        
        if (state==null|| state.equals(RM_COMMITTED) || 
	  state.equals(RM_ACTIVE)) 
	  throw new XAException(XAException.XAER_PROTO);

        if (state.equals(RM_ABORTED)) {
	  txStates.remove(xid);
	  throw new XAException(XAException.XA_RBROLLBACK);
        }

        if (state.equals(RM_HEURCOMMIT)) 
	  throw new XAException(XAException.XA_HEURCOM);

        txStates.remove(xid);
        recoverlist.remove ( xid );
    }
    
    /**
     *@see XAResource
     */
     
    public void forget(Xid xid){

        txStates.remove(xid);
        recoverlist.remove ( xid );
    }
    
    /**
     *@see XAResource
     */
     
    public int getTransactionTimeout() throws XAException{

        return timeout;
    }
    
    /**
     *@see XAResource
     */
     
    public boolean setTransactionTimeout(int time) throws XAException{

        if (time<0) 
	  throw new XAException(XAException.XAER_INVAL);

        timeout=time;
        return true;
    }
    
    /**
     *@see XAResource
     */
     
    public boolean isSameRM(XAResource res) throws XAException{
        
        if (res!=null && equals(res)) 
	  return true;
        else 
	  return false;
    }
    
    /**
     *Simulate recovery. 
     *This method returns an array containing ALL prepared or heuristic
     *transactions (irrespective of the flag value).
     *NOTE: this method only works on an active instance: there is NO
     *persistence in the underlying class. Therefore, recovery simlution
     *only works if the SAME instance of this class is used as the one
     *that did the original prepare/commit work for these Xids.
     *@return Xid[] The array of all transactions as specified above.
     */
     
    public Xid[] recover(int flag) throws XAException {
        Object[] arr = new Xid[1];
        if ( recoverlist.isEmpty() ) 
            return null;
        
        arr = recoverlist.toArray ( arr );
        recoverlist = new Vector();
        return ( Xid[] ) arr;
    }
    
}


