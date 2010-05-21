package com.atomikos.icatch.trmi;
import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.UserTransactionService;

 /**
  *Copyright &copy; 2001-2004, Atomikos. All rights reserved.
  *
  *A facade class for the Trmi TM framework. Allows easy integration 
  *of the trmi TM (standalone) in applications.
  *@deprecated As from release 1.30, an instance of the bean class
  *com.atomikos.icatch.UserTransactionServiceImp does all of this.
  */
  
public final class TrmiConfiguration
{
      
      /**
       *The System property name that holds the path to the 
       *trmi properties file, needed for default initialization
       *of the trmi config.
       *
       */
       
     public static final String FILE_PATH_PROPERTY_NAME =
        com.atomikos.icatch.config.UserTransactionServiceImp.FILE_PATH_PROPERTY_NAME;
      
      /**
       *The default trmi properties file name, assumed to be in 
       *the current directory and to be used in case the 
       *property <b>FILE_PATH_PROPERTY_NAME</b> is not set.
       */
       
     public  static final String DEFAULT_PROPERTIES_FILE_NAME = 
        com.atomikos.icatch.config.UserTransactionServiceImp.DEFAULT_PROPERTIES_FILE_NAME;
       
    
      /**
       *The system property name that indicates that NO
       *configuration file should be used. In this case, default properties
       *will be used, and overridden by any system-set properties.
       *To use this option, just set the System property with this name
       *to an arbitrary value.
       */
       
     public static final String NO_FILE_PROPERTY_NAME =
        com.atomikos.icatch.config.UserTransactionServiceImp.NO_FILE_PROPERTY_NAME;
      
      private static UserTransactionService uts_ = 
          new com.atomikos.icatch.config.UserTransactionServiceImp();
       

      /**
        *Get the UserTransactionManager instance for the configuration.
        *
        *@return UserTransactionManager The UserTransactionManager 
        */    
      
      public static final UserTransactionService
      getUserTransactionService()
      {
          return uts_; 
      }
      
       /**
        *Create a TSInitInfo object for this configuration.
        *@return TSInitInfo The initialization object.
        */
        
      public static final TSInitInfo createTSInitInfo()
      {
          return uts_.createTSInitInfo();
      }

      
 
}
