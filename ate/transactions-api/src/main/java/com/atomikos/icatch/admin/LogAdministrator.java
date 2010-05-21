package com.atomikos.icatch.admin;


 /**
  *
  *
  *This interface is the representation of an administrator facility for 
  *log inspection and termination of active transactions.
  *Different implementations are possible, local OR distributed.
  *This way, administration can be done from one and the same machine, 
  *if needed.
  */

public interface LogAdministrator 
{
     /**
      *Register (add) a LogControl instance to the administrator.
      *This method is typically called right after initialization of the 
      *transaction service.
      *
      *@param control The LogControl instance.
      */
      
    public void registerLogControl ( LogControl control );
    
     /**
      *Deregister (remove) a LogControl instance from the
      *administrator. 
      *This method is typically called at shutdown of the transaction
      *service.
      *
      *@param control The LogControl instance to remove. Does nothing
      *if the control is not registered.
      */
      
    public void deregisterLogControl ( LogControl control ); 
}
