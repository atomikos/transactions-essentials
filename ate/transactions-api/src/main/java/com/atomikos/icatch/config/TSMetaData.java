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
