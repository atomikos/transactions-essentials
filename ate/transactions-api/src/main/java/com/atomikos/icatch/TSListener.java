package com.atomikos.icatch;

import java.util.Properties;

 /**
  *
  *
  *A listener interface for transaction service startup and shutdown events.
  *Instances can register themselves in order to be notified about
  *recovery and shutdown events.
  */

public interface TSListener
{
     /**
      *Called before and after initialization.
      *@param before True indicates that  initialization is about to start.
      *False indicates that initialization has finished. This means that 
      *recovery has been done and the transaction service is now 
      *ready to start new transactions.
      *@param properties The initialization properties.
      */
      
    public void init ( boolean before , Properties properties );
    
     /** 
      *Called before and after shutdown.
      *@param before True if shutdown is about to start.
      *False if shutdown has finished.
      */
      
    public void shutdown ( boolean before ); 
}
