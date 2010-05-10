//$Id: TSListener.java,v 1.1.1.1 2006/08/29 10:01:08 guy Exp $
//$Log: TSListener.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:08  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:40  guy
//Initial import.
//
//Revision 1.2  2006/04/14 12:45:21  guy
//Added properties to TSListener init callback.
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
//Revision 1.1.1.1  2006/03/09 14:59:22  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.3  2005/08/09 07:18:07  guy
//Correted javadoc comments.
//
//Revision 1.2  2005/08/05 15:03:28  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.1.4.1  2004/06/14 08:09:09  guy
//Merged redesign2002 with redesign2003.
//
//Revision 1.1.2.1  2002/10/28 11:00:03  guy
//Improved design: added TSListener mechanism, which is used at recovery of compensators. Improved handling of compensation (orphan detection).
//

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
