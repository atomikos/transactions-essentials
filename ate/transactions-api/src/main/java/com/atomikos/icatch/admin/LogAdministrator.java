//$Id: LogAdministrator.java,v 1.1.1.1 2006/08/29 10:01:08 guy Exp $
//$Log: LogAdministrator.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:08  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:40  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:34  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:29  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:57  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:23  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.2  2004/10/12 13:03:30  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.1  2002/01/23 11:39:42  guy
//Added admin package to CVS.
//

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
