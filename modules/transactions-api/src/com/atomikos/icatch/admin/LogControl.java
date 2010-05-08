//$Id: LogControl.java,v 1.1.1.1 2006/08/29 10:01:08 guy Exp $
//$Log: LogControl.java,v $
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
//Revision 1.1.1.1  2006/03/22 13:46:58  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:23  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.2  2004/10/12 13:03:30  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.1  2002/01/23 11:39:43  guy
//Added admin package to CVS.
//

package com.atomikos.icatch.admin;

 /**
  *
  *
  *This is the admin interface that is exposed to LogAdministrators.
  *It allows control over the transaction logs.
  */

public interface LogControl 
{
         /**
          *Get a list of active transactions.
          *@return AdminTransaction[] The list of active transactions,   
          *or an empty array if none.
          */
            
        public AdminTransaction[] getAdminTransactions();
        
         /** 
          *Get the list of active transactions for a given set of tids only.
          *
          *@param tids The list of previously gotten
          *identifiers to restrict the returned list.
          *@return AdminTransaction[] The list of transactions. This list 
          *may not include ALL tids, because those transactions that 
          *terminated in the meantime will not be returned.
          */
          
        public AdminTransaction[] getAdminTransactions ( String[] tids );
        
         
    
}
