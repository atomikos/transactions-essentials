//$Id: TSMetaData.java,v 1.1.1.1 2006/08/29 10:01:08 guy Exp $
//$Log: TSMetaData.java,v $
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
//Revision 1.3  2005/08/05 15:03:28  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.2  2004/10/12 13:03:26  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.1  2002/03/11 01:36:57  guy
//Added MetaData for the UserTransactionService.
//

package com.atomikos.icatch.config;
 /**
  *
  *
  *A meta data interface for retrieving information about the
  *Transaction Service.
  *
  *@deprecated 
  */

public interface TSMetaData
{
     /**
      *Get the JTA version that is supported.
      *@deprecated 
      *@return String A string representation of the JTA supported.
      */
      
    public String getJtaVersion();
     
     /**
      *Get the release version.
      *@return String A string representation of the release version.
      */
      
    public String getReleaseVersion();
    
     /**
      *Test if import of transactions is supported.
      *@return boolean True iff import of a Propagation is supported.
      */
      
    public boolean supportsImport();
    
     /**
      *Test if export of transactions is supported.
      *@return boolean True iff export is supported.
      */
      
    public boolean supportsExport();
     /**
      *Get the full product name.
      *@return String The full product name.
      */
      
    public String getProductName();
}
