//$Id: TrmiConfiguration.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//$Log: TrmiConfiguration.java,v $
//Revision 1.1.1.1  2006/10/02 15:21:17  guy
//Import into CVS.
//
//Revision 1.1.1.1  2006/04/29 08:55:45  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:38  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:33  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:47:01  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:33  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.11  2004/10/11 13:39:43  guy
//Fixed javadoc and EOL delimiters.
//
//$Id: TrmiConfiguration.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Revision 1.10  2004/09/28 11:49:38  guy
//$Id: TrmiConfiguration.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//
//$Id: TrmiConfiguration.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Re-added this class for backward compatibility.
//$Id: TrmiConfiguration.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//
//$Id: TrmiConfiguration.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Revision 1.8  2003/03/11 06:39:16  guy
//$Id: TrmiConfiguration.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Merged in changes from transactionsJTA100 branch.
//$Id: TrmiConfiguration.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//
//Revision 1.7.4.1  2003/01/31 15:45:34  guy
//Adapted to set/get Properties in AbstractUserTransactionServiceFactory.
//
//Revision 1.7  2002/01/07 12:26:39  guy
//Updated UserTransactionImp to check license, and to allow system propery
//settings.
//
//Revision 1.6  2001/12/30 12:35:41  guy
//Updated to use UserTransactionService interface.
//
//Revision 1.5  2001/12/20 10:16:42  guy
//Updated console file to be opened in append mode.
//
//Revision 1.4  2001/12/20 10:10:30  guy
//Changed shutdown to static method.
//
//Revision 1.3  2001/12/20 09:02:07  guy
//Updated TrmiConfiguration to call init() on tm.
//
//Revision 1.2  2001/12/06 15:41:58  guy
//Added installation of Importing and Exporting TM in Configuration class.
//

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
