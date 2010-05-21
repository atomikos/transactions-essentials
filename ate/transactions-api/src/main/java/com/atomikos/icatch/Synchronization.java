package com.atomikos.icatch;


 /**
  *
  *
  *A synchronization inferface for tx termination callbacks.
  */
  
 public interface Synchronization 
 extends java.io.Serializable 
 {
    /**
     *Called before prepare decision is made.
     */
     
    public void beforeCompletion ();
    
    /**
     *Called after the overall outcome  is known.
     *
     *@param txstate The state of the coordinator after preparing.
     *Equals either null ( readonly ), TxState.COMMITTING  or TxState.ABORTING.
     */
     
    public void afterCompletion ( Object txstate );	
 }
