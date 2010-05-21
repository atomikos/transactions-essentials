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
