/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.admin;


 /**
  * The system's admin interface that is exposed to LogAdministrators.
  * It allows control over the transaction logs.
  */

public interface LogControl 
{
 
         /** 
          * Gets the list of active transactions for a given set of 
          * transaction identifiers only.
          *
          * @param tids The list of previously gotten
          * identifiers to restrict the returned list.
          * 
          * @return AdminTransaction[] The list of transactions. This list 
          * may not include ALL tids, because those transactions that 
          * terminated in the meantime will not be returned.
          */
          
         AdminTransaction[] getAdminTransactions(String... tids );
        
         
    
}
