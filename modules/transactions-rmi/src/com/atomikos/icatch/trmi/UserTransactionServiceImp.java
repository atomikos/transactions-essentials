//$Id: UserTransactionServiceImp.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//$Log: UserTransactionServiceImp.java,v $
//Revision 1.1.1.1  2006/10/02 15:21:17  guy
//Import into CVS.
//
//Revision 1.1.1.1  2006/04/29 08:55:45  guy
//Initial import.
//
//Revision 1.2  2006/04/11 11:42:56  guy
//Extracted init properties as constants and replaced all literal references.
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
//Revision 1.3  2006/03/21 16:14:41  guy
//Added setter for active recovery.
//
//Revision 1.2  2006/03/21 13:24:01  guy
//Introduced active recovery and CompTx properties as meta-tags.
//
//Revision 1.1.1.1  2006/03/09 14:59:33  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.60  2005/10/28 15:27:12  guy
//Added WSAT transport.
//
//Revision 1.59  2005/09/20 07:28:53  guy
//Corrected BUG: trustClientTM was not used for CommitServer.
//
//Revision 1.58  2005/09/03 08:35:08  guy
//Added support for multiple protocols, and one HTTP configuration.
//
//Revision 1.57  2005/08/30 07:10:04  guy
//Debugged class loading in webapp/jetty.
//
//Revision 1.56  2005/08/29 06:53:51  guy
//Added use of threads init parameter (for SOAP handlers).
//
//Revision 1.55  2005/08/27 11:29:09  guy
//Modified to use new Sender paradigm.
//
//Revision 1.54  2005/08/23 13:06:46  guy
//Updated SOAP init parameters.
//
//Revision 1.53  2005/08/23 10:20:24  guy
//Updated release number.
//
//Revision 1.52  2005/08/19 07:45:54  guy
//Corrected to add transport to configuration too.
//
//Revision 1.51  2005/08/14 21:31:03  guy
//Debugged.
//
//Revision 1.50  2005/08/13 13:41:50  guy
//Added test feedback and some logging.
//
//Revision 1.49  2005/08/06 07:37:21  guy
//Updated to include installTransactionService call.
//
//Revision 1.48  2005/05/10 08:44:11  guy
//Merged-in changes from Transactions_2_03 branch.
//
//Revision 1.47  2005/05/09 08:07:39  guy
//Added JCA logadministrator to support inbound txs.
//
//Revision 1.46  2005/04/29 15:00:37  guy
//Updated to use new RotatingFileConsole.
//Revision 1.43.2.2  2005/02/05 16:13:18  guy
//Updated release number.
//Now using trimmed properties in TrmiTransactionManager.
//
//Revision 1.45  2005/01/07 17:06:35  guy
//Updated release version.
//
//Revision 1.44  2004/12/10 05:59:05  guy
//Updated to 201
//Revision 1.43.2.1  2004/12/13 19:41:56  guy
//Updated bug fix: synchronized XAResTx map for MM.
//
//Revision 1.43  2004/11/24 09:49:58  guy
//updated exception messages to include more info.
//
//Revision 1.42  2004/10/31 11:03:47  guy
//Added default name to TRMI.
//
//Revision 1.41  2004/10/27 09:40:26  guy
//Updated init to create output and log directories if required.
//
//Revision 1.40  2004/10/25 09:45:55  guy
//Changed property name: enable_logging.
//
//Revision 1.39  2004/10/25 08:46:00  guy
//Updated TODOs
//
//Revision 1.38  2004/10/18 08:48:36  guy
//Added support for disabling recovery.
//
//Revision 1.37  2004/10/14 14:38:57  guy
//Changed to use J2eeUserTransaction, to avoid automatic init.
//
//Revision 1.36  2004/10/08 09:28:16  guy
//Changed product name for license check.
//
//Revision 1.35  2004/10/01 12:53:57  guy
//Updated todos.
//
//Revision 1.34  2004/09/28 11:27:20  guy
//Made startup/shutdown independent of instance on which it is called.
//
//Revision 1.33  2004/09/27 12:39:22  guy
//Removed deprecated code fragments.
//
//Revision 1.32  2004/09/27 12:27:34  guy
//Adapted to singleton UserTransactionServer.
//
//Revision 1.31  2004/09/27 11:36:58  guy
//Added default name generation if no unique name specified.
//
//Revision 1.30  2004/09/25 09:19:13  guy
//Inserted comment
//
//Revision 1.29  2004/09/20 14:50:48  guy
//Added trimming.
//
//Revision 1.28  2004/09/18 14:23:50  guy
//Added trim of automatic registration property.
//
//Revision 1.27  2004/09/18 12:09:44  guy
//Added automatic resource registration mode.
//
//Revision 1.26  2004/09/17 16:41:55  guy
//Improved log methods in Configuration.
//
//Revision 1.25  2004/09/09 15:07:10  guy
//Update release number.
//
//Revision 1.24  2004/09/09 13:10:29  guy
//Regenerated stubs (marshal errros).
//Corrected bug in ParticipantProxy: lookup and cast in one go
//seemed to throw non-caught exception during recover()?
//
//Revision 1.23  2004/09/06 09:27:25  guy
//Adapted for new recovery.
//
//Revision 1.22  2004/09/01 13:39:23  guy
//Merged changes from TransactionsRMI 1.22.
//
//Revision 1.21  2004/08/30 07:19:21  guy
//Corrected license lookup.
//
//Revision 1.20  2004/03/25 12:54:11  guy
//Added support for max active transactions.
//
//Revision 1.19  2004/03/22 15:38:14  guy
//Merged-in changes from branch redesign-4-2003.
//
//Revision 1.18  2003/09/01 15:28:04  guy
//Modified exception wrapping in init: more verbose messages.
//Added JRMP native stubs for WebLogic and JBoss compatibility.
//
//Revision 1.17  2003/08/27 19:02:53  guy
//Corrected bug in deserialization of CompositeTransactionProxy
//
//Revision 1.16  2003/08/27 17:28:18  guy
//Changed createTSInitInfo to use getProperties, not getDefaultProperties
//
//Revision 1.15  2003/08/27 17:16:56  guy
//Set JNDI initial context URL to default value
//
//Revision 1.14  2003/08/27 06:24:07  guy
//Adapted to RMI-IIOP.
//
//$Id: UserTransactionServiceImp.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//$Log: UserTransactionServiceImp.java,v $
//Revision 1.1.1.1  2006/10/02 15:21:17  guy
//Import into CVS.
//
//Revision 1.1.1.1  2006/04/29 08:55:45  guy
//Initial import.
//
//Revision 1.2  2006/04/11 11:42:56  guy
//Extracted init properties as constants and replaced all literal references.
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
//Revision 1.3  2006/03/21 16:14:41  guy
//Added setter for active recovery.
//
//Revision 1.2  2006/03/21 13:24:01  guy
//Introduced active recovery and CompTx properties as meta-tags.
//
//Revision 1.1.1.1  2006/03/09 14:59:33  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.60  2005/10/28 15:27:12  guy
//Added WSAT transport.
//
//Revision 1.59  2005/09/20 07:28:53  guy
//Corrected BUG: trustClientTM was not used for CommitServer.
//
//Revision 1.58  2005/09/03 08:35:08  guy
//Added support for multiple protocols, and one HTTP configuration.
//
//Revision 1.57  2005/08/30 07:10:04  guy
//Debugged class loading in webapp/jetty.
//
//Revision 1.56  2005/08/29 06:53:51  guy
//Added use of threads init parameter (for SOAP handlers).
//
//Revision 1.55  2005/08/27 11:29:09  guy
//Modified to use new Sender paradigm.
//
//Revision 1.54  2005/08/23 13:06:46  guy
//Updated SOAP init parameters.
//
//Revision 1.53  2005/08/23 10:20:24  guy
//Updated release number.
//
//Revision 1.52  2005/08/19 07:45:54  guy
//Corrected to add transport to configuration too.
//
//Revision 1.51  2005/08/14 21:31:03  guy
//Debugged.
//
//Revision 1.50  2005/08/13 13:41:50  guy
//Added test feedback and some logging.
//
//Revision 1.49  2005/08/06 07:37:21  guy
//Updated to include installTransactionService call.
//
//Revision 1.48  2005/05/10 08:44:11  guy
//Merged-in changes from Transactions_2_03 branch.
//
//Revision 1.47  2005/05/09 08:07:39  guy
//Added JCA logadministrator to support inbound txs.
//
//Revision 1.46  2005/04/29 15:00:37  guy
//Updated to use new RotatingFileConsole.
//Revision 1.43.2.2  2005/02/05 16:13:18  guy
//Updated release number.
//Now using trimmed properties in TrmiTransactionManager.
//
//Revision 1.45  2005/01/07 17:06:35  guy
//Updated release version.
//Revision 1.43.2.1  2004/12/13 19:41:56  guy
//Updated bug fix: synchronized XAResTx map for MM.
//
//Revision 1.44  2004/12/10 05:59:05  guy
//Updated to 201
//Revision 1.43  2004/11/24 09:49:58  guy
//updated exception messages to include more info.
//
//Revision 1.43  2004/11/24 09:49:58  guy
//updated exception messages to include more info.
//Revision 1.42  2004/10/31 11:03:47  guy
//Added default name to TRMI.
//
//Revision 1.42  2004/10/31 11:03:47  guy
//Added default name to TRMI.
//Revision 1.41  2004/10/27 09:40:26  guy
//Updated init to create output and log directories if required.
//
//Revision 1.41  2004/10/27 09:40:26  guy
//Updated init to create output and log directories if required.
//Revision 1.40  2004/10/25 09:45:55  guy
//Changed property name: enable_logging.
//
//Revision 1.40  2004/10/25 09:45:55  guy
//Changed property name: enable_logging.
//Revision 1.39  2004/10/25 08:46:00  guy
//Updated TODOs
//
//Revision 1.39  2004/10/25 08:46:00  guy
//Updated TODOs
//Revision 1.38  2004/10/18 08:48:36  guy
//Added support for disabling recovery.
//
//Revision 1.38  2004/10/18 08:48:36  guy
//Added support for disabling recovery.
//Revision 1.37  2004/10/14 14:38:57  guy
//Changed to use J2eeUserTransaction, to avoid automatic init.
//
//Revision 1.37  2004/10/14 14:38:57  guy
//Changed to use J2eeUserTransaction, to avoid automatic init.
//Revision 1.36  2004/10/08 09:28:16  guy
//Changed product name for license check.
//
//Revision 1.36  2004/10/08 09:28:16  guy
//Changed product name for license check.
//Revision 1.35  2004/10/01 12:53:57  guy
//Updated todos.
//
//Revision 1.34  2004/09/28 11:27:20  guy
//Made startup/shutdown independent of instance on which it is called.
//
//Revision 1.33  2004/09/27 12:39:22  guy
//Removed deprecated code fragments.
//
//Revision 1.32  2004/09/27 12:27:34  guy
//Adapted to singleton UserTransactionServer.
//
//Revision 1.31  2004/09/27 11:36:58  guy
//Added default name generation if no unique name specified.
//
//Revision 1.30  2004/09/25 09:19:13  guy
//Inserted comment
//
//Revision 1.29  2004/09/20 14:50:48  guy
//Added trimming.
//
//Revision 1.28  2004/09/18 14:23:50  guy
//Added trim of automatic registration property.
//
//Revision 1.27  2004/09/18 12:09:44  guy
//Added automatic resource registration mode.
//
//Revision 1.26  2004/09/17 16:41:55  guy
//Improved log methods in Configuration.
//
//Revision 1.25  2004/09/09 15:07:10  guy
//Update release number.
//
//Revision 1.24  2004/09/09 13:10:29  guy
//Regenerated stubs (marshal errros).
//Corrected bug in ParticipantProxy: lookup and cast in one go
//seemed to throw non-caught exception during recover()?
//
//Revision 1.23  2004/09/06 09:27:25  guy
//Adapted for new recovery.
//
//Revision 1.22  2004/09/01 13:39:23  guy
//Merged changes from TransactionsRMI 1.22.
//
//$Id: UserTransactionServiceImp.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Revision 1.21  2004/08/30 07:19:21  guy
//$Id: UserTransactionServiceImp.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Corrected license lookup.
//$Id: UserTransactionServiceImp.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//
//$Id: UserTransactionServiceImp.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Revision 1.20  2004/03/25 12:54:11  guy
//$Id: UserTransactionServiceImp.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Added support for max active transactions.
//$Id: UserTransactionServiceImp.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//
//$Id: UserTransactionServiceImp.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Revision 1.19  2004/03/22 15:38:14  guy
//$Id: UserTransactionServiceImp.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Merged-in changes from branch redesign-4-2003.
//$Id: UserTransactionServiceImp.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//
//$Id: UserTransactionServiceImp.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Revision 1.13.2.6  2004/01/14 10:38:51  guy
//$Id: UserTransactionServiceImp.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//*** empty log message ***
//$Id: UserTransactionServiceImp.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//
//$Id: UserTransactionServiceImp.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Revision 1.13.2.5  2003/11/16 09:03:16  guy
//$Id: UserTransactionServiceImp.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Corrected BUG: output dir property was not used.
//$Id: UserTransactionServiceImp.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//
//$Id: UserTransactionServiceImp.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Revision 1.13.2.4  2003/06/20 16:31:53  guy
//$Id: UserTransactionServiceImp.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//*** empty log message ***
//$Id: UserTransactionServiceImp.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//
//$Id: UserTransactionServiceImp.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Revision 1.13.2.3  2003/06/03 08:58:49  guy
//$Id: UserTransactionServiceImp.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Continued rewrite of UserTx.
//$Id: UserTransactionServiceImp.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//
//$Id: UserTransactionServiceImp.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Revision 1.13.2.2  2003/05/30 15:18:59  guy
//$Id: UserTransactionServiceImp.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Changed UserTransactionFactory.getUserTransaction: added Properties arg.
//$Id: UserTransactionServiceImp.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//
//$Id: UserTransactionServiceImp.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Revision 1.13.2.1  2003/05/22 15:24:53  guy
//$Id: UserTransactionServiceImp.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Renamed Configuration to UserTransactionServiceFactory.
//$Id: UserTransactionServiceImp.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//
//$Id: UserTransactionServiceImp.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Revision 1.13  2003/03/11 06:39:16  guy
//$Id: UserTransactionServiceImp.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Merged in changes from transactionsJTA100 branch.
//$Id: UserTransactionServiceImp.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//
//Revision 1.12.2.6  2003/01/31 15:45:34  guy
//Adapted to set/get Properties in AbstractUserTransactionServiceFactory.
//
//Revision 1.12.2.5  2003/01/29 17:20:08  guy
//Adapted to use JNDI binding instead of Naming of RMI.
//
//Revision 1.12.2.4  2002/11/20 18:35:12  guy
//Updated to use rmi remote UserTransaction.
//Added stub files explicitly, to allow make complete to work.
//
//Revision 1.12.2.3  2002/11/18 18:52:28  guy
//Corrected BUG: participant proxy did not restore server_ after readin.
//UserTransaction now supports serial JTA txs by default.
//
//Revision 1.12.2.2  2002/11/18 17:51:08  guy
//Updated test and version ID.
//
//Revision 1.12.2.1  2002/11/16 16:27:07  guy
//Added getUserTransaction and remote support.
//
//Revision 1.12  2002/03/22 11:43:04  guy
//Updated product name.
//
//Revision 1.11  2002/03/21 17:47:42  guy
//Changed LocalLogAdministrator to use UserTransactionService for shutdown, in order to propagate shutdown to resources.
//
//Revision 1.10  2002/03/11 01:37:09  guy
//Added MetaData for the UserTransactionService.
//
//Revision 1.9  2002/03/03 09:19:36  guy
//Changed license file lookup from filename to URL, this allows checking in JAR files as well!
//
//Revision 1.8  2002/01/23 08:43:15  guy
//Updated to new license: includes product name.
//
//Revision 1.7  2002/01/22 14:17:53  guy
//Updated to use new License class, and added checkpoint support.
//
//Revision 1.6  2002/01/10 11:03:10  guy
//Corrected writeExternal bug in ParticipantProxy and adapted UserTransactionImp to use the new LogAdministrators.
//
//Revision 1.5  2002/01/07 16:14:35  guy
//*** empty log message ***
//
//Revision 1.3  2002/01/07 13:48:15  guy
//Updated to set default value for tm_unique_name.
//Needed because ALL property names must be in default, or
//System properties option will not work fine.
//
//Revision 1.2  2002/01/07 12:26:39  guy
//Updated UserTransactionImp to check license, and to allow system propery
//settings.
//
//Revision 1.1  2001/12/30 12:35:41  guy
//Updated to use UserTransactionService interface.
//

package com.atomikos.icatch.trmi;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Stack;

import javax.naming.Context;
import javax.transaction.UserTransaction;

import com.atomikos.diagnostics.Console;
import com.atomikos.diagnostics.RotatingFileConsole;
import com.atomikos.diagnostics.Slf4jConsole;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.admin.LogAdministrator;
import com.atomikos.icatch.admin.imp.LocalLogAdministrator;
import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.TSMetaData;
import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;
import com.atomikos.icatch.config.imp.TSInitInfoImp;
import com.atomikos.icatch.config.imp.TSMetaDataImp;
import com.atomikos.icatch.jca.XidLogAdministrator;
import com.atomikos.icatch.jta.AbstractJtaUserTransactionService;
import com.atomikos.icatch.jta.J2eeUserTransaction;
import com.atomikos.icatch.jta.JTA;
import com.atomikos.icatch.jta.TransactionManagerImp;
import com.atomikos.icatch.jta.UserTransactionServerImp;
import com.atomikos.icatch.system.Configuration;
import com.atomikos.license.License;
import com.atomikos.license.LicenseException;
import com.atomikos.persistence.StateRecoveryManager;
import com.atomikos.persistence.imp.FileLogStream;
import com.atomikos.persistence.imp.StateRecoveryManagerImp;
import com.atomikos.persistence.imp.StreamObjectLog;
import com.atomikos.persistence.imp.VolatileStateRecoveryManager;
import com.atomikos.util.ClassLoadingHelper;

class UserTransactionServiceImp
extends AbstractJtaUserTransactionService
{
    private static final String PRODUCT_NAME = "ExtremeTransactions";
    //the product name as it should be in the license

    
    private static final String VERSION = "3.7M1.0";
  
    
     private static Properties getDefaultProperties()
     {
         //
          Properties ret = new Properties ();
          ret.setProperty ( UserTransactionServiceFactory.TRUST_CLIENT_TM_PROPERTY_NAME , "false" );
          ret.setProperty ( AbstractUserTransactionServiceFactory.CONSOLE_FILE_NAME_PROPERTY_NAME , "tm.out" );
          ret.setProperty ( AbstractUserTransactionServiceFactory.OUTPUT_DIR_PROPERTY_NAME ,  "." + File.separator );
          ret.setProperty ( AbstractUserTransactionServiceFactory.LOG_BASE_DIR_PROPERTY_NAME , "." + File.separator );
		  ret.setProperty ( AbstractUserTransactionServiceFactory.CONSOLE_LOG_LEVEL_PROPERTY_NAME , "WARN");
          ret.setProperty ( AbstractUserTransactionServiceFactory.SERIAL_JTA_TRANSACTIONS_PROPERTY_NAME , "true" );
          ret.setProperty ( AbstractUserTransactionServiceFactory.LOG_BASE_NAME_PROPERTY_NAME , "tmlog" );
          ret.setProperty ( AbstractUserTransactionServiceFactory.MAX_ACTIVES_PROPERTY_NAME , "50" );
          ret.setProperty ( AbstractUserTransactionServiceFactory.MAX_TIMEOUT_PROPERTY_NAME , "60000" );
          ret.setProperty ( AbstractUserTransactionServiceFactory.TM_UNIQUE_NAME_PROPERTY_NAME , getDefaultName() );
          ret.setProperty ( AbstractUserTransactionServiceFactory.CHECKPOINT_INTERVAL_PROPERTY_NAME , "500" );
          ret.setProperty ( AbstractUserTransactionServiceFactory.CLIENT_DEMARCATION_PROPERTY_NAME , "true" );
          ret.setProperty ( AbstractUserTransactionServiceFactory.RMI_EXPORT_CLASS_PROPERTY_NAME , "none");
          ret.setProperty ( AbstractUserTransactionServiceFactory.THREADED_2PC_PROPERTY_NAME , "true");
          ret.setProperty ( AbstractUserTransactionServiceFactory.REGISTER_SHUTDOWN_HOOK_PROPERTY_NAME , "false" );
          ret.setProperty( AbstractUserTransactionServiceFactory.DEFAULT_JTA_TIMEOUT_PROPERTY_NAME , "10000" );
		  ret.setProperty ( Context.INITIAL_CONTEXT_FACTORY , 
			 "com.sun.jndi.cosnaming.CNCtxFactory" );
		  ret.setProperty ( Context.PROVIDER_URL , "iiop://localhost:1050" );
		  
		  //
		  ret.setProperty ( AbstractUserTransactionServiceFactory.AUTOMATIC_RESOURCE_REGISTRATION_PROPERTY_NAME , "true" );
		  ret.setProperty ( AbstractUserTransactionServiceFactory.ENABLE_LOGGING_PROPERTY_NAME , "true" );
		  ret.setProperty ( AbstractUserTransactionServiceFactory.CONSOLE_FILE_LIMIT_PROPERTY_NAME , "-1" );
		  ret.setProperty ( AbstractUserTransactionServiceFactory.CONSOLE_FILE_COUNT_PROPERTY_NAME , "1" );
		  
		  //comma-separated string of protocols to install: allows license-based
	      //limitation of soap capabilities
		  ret.setProperty ( UserTransactionServiceFactory.SOAP_COMMIT_PROTOCOLS_PROPERTY_NAME , "atomikos,wsat");
		  ret.setProperty ( UserTransactionServiceFactory.SOAP_HOST_ADDRESS_PROPERTY_NAME , getHostAddress() );
		  ret.setProperty ( UserTransactionServiceFactory.SOAP_PORT_PROPERTY_NAME , "8088");
		  ret.setProperty ( UserTransactionServiceFactory.SECURE_HTTP_PROPERTY_NAME , "false");
		  
          return ret;
      }

     
     private static String getDefaultProperty ( String name ) 
     {
    	 return getDefaultProperties().getProperty ( name );
     } 
     
     private static void warnIfEqualsDefaultValue ( String propertyName , Properties p )
     {
    	 String value = getDefaultProperty ( propertyName );
    	if ( value != null && value.equals ( p.getProperty ( propertyName )) ) {
    			Configuration.logWarning ( "Init property " + propertyName + ": using default - you may want to override this..." );
    		
    	}
     }
      
      //private TrmiTransactionManager tm_;
      
      private File lockfile_;
      //sets lock to prevent double startup
      
      private FileOutputStream lockfilestream_ = null;
      private FileLock lock_ = null;

      
      //private TSInitInfo info_;
      //the info object

      private Properties properties_;
      //the properties

      UserTransactionServiceImp ( Properties properties )
      {
    	  
          properties_ = getDefaultProperties();
          Enumeration enumm = properties.propertyNames();

          while ( enumm.hasMoreElements() ) {
               String name = ( String ) enumm.nextElement();
               String property = properties.getProperty ( name );
               properties_.setProperty ( name , property );
          }

          if ( System.getProperty (
                 com.atomikos.icatch.config.UserTransactionServiceImp.NO_FILE_PROPERTY_NAME )
                 != null ) {

             enumm = properties_.propertyNames ();
             Stack names = new Stack ();
             while ( enumm.hasMoreElements () ) {
                 String name = ( String ) enumm.nextElement ();
                 names.push ( name );
             }
             while ( !names.empty () ) {
                 String name = ( String ) names.pop ();
                 if ( System.getProperty ( name ) != null ) {
                     properties_.setProperty ( name , System.getProperty ( name ) );
                 }
             }


         }

      }

      private Properties getProperties()
      {

            return properties_;
      }
      
    


       /**
        *Creates a default TM instance.
        *
        *@param p The properties to use.
        *@return TrmiTransactionManager The default instance.
        *@exception IOException On IO error.
        *@exception FileNotFoundException If the config file could not
        *be found.
        */
        
      private  TrmiTransactionManager createDefault ( Properties p )
      throws IOException, FileNotFoundException
      {
        TrmiTransactionManager ret = null;
        
        warnIfEqualsDefaultValue ( UserTransactionServiceFactory.SOAP_HOST_ADDRESS_PROPERTY_NAME , p );
       
		String consoleDir = getTrimmedProperty ( AbstractUserTransactionServiceFactory.OUTPUT_DIR_PROPERTY_NAME , p );
		consoleDir = findOrCreateFolder ( consoleDir );
		String consolePath = consoleDir + getTrimmedProperty ( AbstractUserTransactionServiceFactory.CONSOLE_FILE_NAME_PROPERTY_NAME, p);

		String limitString = getTrimmedProperty ( AbstractUserTransactionServiceFactory.CONSOLE_FILE_LIMIT_PROPERTY_NAME , p );
		String countString = getTrimmedProperty ( AbstractUserTransactionServiceFactory.CONSOLE_FILE_COUNT_PROPERTY_NAME , p );
		int limit = Integer.parseInt ( limitString );
		int count = Integer.parseInt ( countString );
		RotatingFileConsole console = new RotatingFileConsole ( consolePath , limit , count );
		String logLevel = getTrimmedProperty ( AbstractUserTransactionServiceFactory.CONSOLE_LOG_LEVEL_PROPERTY_NAME , p);
		if ( logLevel != null ) logLevel = logLevel.trim();
		int level = Console.WARN;
		if ( "INFO".equals ( logLevel ) ) level = Console.INFO;
		else if ( "DEBUG".equals ( logLevel ) ) level = Console.DEBUG;
		console.setLevel ( level );
		Configuration.addConsole ( console );         
		
		License.setConsole ( console );

		License license = null;
		try {
			license = License.createLicense ( License.PRODUCT_NAME_PROPERTY_NAME );
			license.checkLocalHost ( PRODUCT_NAME );
		} catch ( LicenseException e ) {
			String msg = "ERROR:\nATOMIKOS: NO VALID LICENSE FOUND!\n" +
			"PLEASE CONTACT sales@atomikos.com\n" + 
			"FOR A LICENSE TO USE THIS PRODUCT, OR DOWNGRADE TO\n" +
			"TRANSACTIONSESSENTIALS (WITHOUT PRODUCTION SUPPORT)";
			Configuration.logWarning ( msg , e );
			Stack errors = new Stack();
			errors.push ( e );
			throw new SysException ( 
					msg , errors );

		}
		
		license.filterProductFeatures ( p );
		
        try {
        	//use improved class loading
			ClassLoadingHelper.loadClass ( "org.slf4j.Logger" );
        	Slf4jConsole slf4jConsole = new Slf4jConsole();
        	slf4jConsole.setLevel ( level );
        	Configuration.addConsole ( slf4jConsole );
        } catch (ClassNotFoundException ex) {
        	Configuration.logDebug("cannot load SLF4J, skipping this console", ex);
		}
		
		
		  
          String logname = getTrimmedProperty ( AbstractUserTransactionServiceFactory.LOG_BASE_NAME_PROPERTY_NAME,p );
          String logdir = getTrimmedProperty ( AbstractUserTransactionServiceFactory.LOG_BASE_DIR_PROPERTY_NAME , p );
          logdir = findOrCreateFolder ( logdir );
          String tmName = getTrimmedProperty ( AbstractUserTransactionServiceFactory.TM_UNIQUE_NAME_PROPERTY_NAME , p);
          //set system props for trmi transaction manager to use JNDI
          String provider = getTrimmedProperty ( Context.PROVIDER_URL , p );
          String factory = getTrimmedProperty ( Context.INITIAL_CONTEXT_FACTORY , p );
          
          if ( provider == null || provider.equals ( "" ) )
              throw new SysException ( "Context.PROVIDER_URL not set!" );
          if ( factory == null || factory.equals ( "" ) ) 
              throw new SysException ( 
              "Context.INITIAL_CONTEXT_FACTORY not set!" );
          
          //set JNDI parameters as system properties for 
          //TrmiTransactionManager
         
              
          if ( tmName == null || tmName.equals ( "" ) ) {
          	String msg = "For correct recovery, please set the startup property com.atomikos.icatch.tm_unique_name to a globally unique name!";
          	Configuration.logWarning ( msg );
          	tmName = getDefaultName();
          }
              
          String recoveryPrefs = getTrimmedProperty ( AbstractUserTransactionServiceFactory.ENABLE_LOGGING_PROPERTY_NAME , p );
          boolean enableRecovery = true;
          if ( "false".equals ( recoveryPrefs ) )
          	enableRecovery = false;
          
          //make sure that no other instance is running with the same log
          //by setting a lock file
          lockfile_ = new File ( logdir + logname + ".lck" );
          if ( enableRecovery ) {
         	 //ISSUE 10077: don't complain about lock file if no logging
         	try {
         		lockfilestream_ = new FileOutputStream ( lockfile_ );
         		lock_ = lockfilestream_.getChannel().tryLock();
         		lockfile_.deleteOnExit();
         	} catch ( OverlappingFileLockException failedToGetLock ) {
         		//happens on windows
         		lock_ = null;
         	} catch ( IOException failedToGetLock ) {
         		//happens on windows
         		lock_ = null;
         	}
         	if ( lock_ == null ) {
         		 System.err.println ( "ERROR: the specified log seems to be "
                          + "in use already. Make sure that no other instance is "
                          + "running, or kill any pending process if needed." );
                  throw new RuntimeException ( "Log already in use?" );
         	}
         }

          
          int max = 
              ( new Integer ( getTrimmedProperty ( AbstractUserTransactionServiceFactory.MAX_ACTIVES_PROPERTY_NAME , p ) ) ).intValue();
          long chckpt = 
              ( new Long ( getTrimmedProperty ( AbstractUserTransactionServiceFactory.CHECKPOINT_INTERVAL_PROPERTY_NAME , p ) ) ).longValue();

          FileLogStream logstream = 
              new FileLogStream ( logdir , logname , console );
          StreamObjectLog slog = 
              new StreamObjectLog ( logstream , chckpt , console );
          
         
         StateRecoveryManager recmgr = null;
         if ( enableRecovery )
              recmgr = new StateRecoveryManagerImp ( slog );
         else recmgr = new VolatileStateRecoveryManager();
          
          long timeout = 
              ( new Long ( getTrimmedProperty ( AbstractUserTransactionServiceFactory.MAX_TIMEOUT_PROPERTY_NAME , p ) ) ).longValue();
 

          Boolean trustClient = new Boolean ( getTrimmedProperty ( UserTransactionServiceFactory.TRUST_CLIENT_TM_PROPERTY_NAME , p ) );
          
          String threadedCommitPrefs = getTrimmedProperty ( AbstractUserTransactionServiceFactory.THREADED_2PC_PROPERTY_NAME , p );
          boolean threadedCommit = true;
          if ( "false".equals( threadedCommitPrefs )) threadedCommit = false;
          
          ret = new TrmiTransactionManager ( trustClient.booleanValue() , 
                                                          recmgr , tmName , console , 
                                                          logdir , timeout , max , threadedCommit );
          
          long defaultTimeoutInMillis = (new Long ( getTrimmedProperty (
                  AbstractUserTransactionServiceFactory.DEFAULT_JTA_TIMEOUT_PROPERTY_NAME, p ) )).longValue ();
          int defaultTimeout = 0;

          defaultTimeout = (int) defaultTimeoutInMillis/1000;
          if ( defaultTimeout <= 0 ) {
          	Configuration.logWarning ( "WARNING: " + AbstractUserTransactionServiceFactory.DEFAULT_JTA_TIMEOUT_PROPERTY_NAME + " should be more than 1000 milliseconds - resetting to 10000 milliseconds instead..." );
          	defaultTimeout = 10;
          }

          
          TransactionManagerImp.setDefaultTimeout(defaultTimeout);

		
          //set default serial mode for JTA txs.
          if ( new Boolean ( getTrimmedProperty ( 
                                    AbstractUserTransactionServiceFactory.SERIAL_JTA_TRANSACTIONS_PROPERTY_NAME , p )
                                    ).booleanValue() )
                  TransactionManagerImp.setDefaultSerial ( true );

          return ret;
      }
      
      
   
      public void init ( TSInitInfo info )
      throws SysException
      {
      	
          Properties p = info.getProperties();
          TrmiTransactionManager tm = null;
          
          
          try {
              
			  //add default JCA log administrator
			  Configuration.addLogAdministrator ( XidLogAdministrator.getInstance() );              
              registerTSListener ( new TransportTSListener() );
              
              tm = createDefault ( p );
              tm.init ( p );
              String autoMode = getTrimmedProperty ( AbstractUserTransactionServiceFactory.AUTOMATIC_RESOURCE_REGISTRATION_PROPERTY_NAME , p );
              if ( autoMode != null ) autoMode = autoMode.trim();
              boolean autoRegister = "true".equals ( 
              	autoMode );
              Configuration.installCompositeTransactionManager ( tm );
              com.atomikos.icatch.jta.TransactionManagerImp.
                  installTransactionManager ( tm , autoRegister );
              Configuration.installExportingTransactionManager ( tm );
              Configuration.installImportingTransactionManager ( tm );
              Configuration.installRecoveryService ( tm.getTransactionService() );
              Configuration.installLogControl ( tm.getTransactionService().getLogControl() );
              Configuration.installTransactionService ( tm.getTransactionService() );
              
              Enumeration admins = info.getLogAdministrators();
              while ( admins.hasMoreElements() ) {
                  LogAdministrator admin = 
                      ( LogAdministrator ) admins.nextElement();
                  if ( admin instanceof LocalLogAdministrator ) {
                      LocalLogAdministrator ladmin =
                        (LocalLogAdministrator ) admin;
                      ladmin.init ( this );
                  }
                  
              }
              
              
              
              //lastly, set up UserTransactionServer if client demarcation 
              //is allowed
              boolean clientDemarcation = 
                  new Boolean ( getTrimmedProperty ( AbstractUserTransactionServiceFactory.CLIENT_DEMARCATION_PROPERTY_NAME , p ) ).
                  booleanValue();
                  
             
                  
              if ( clientDemarcation ) {
                  //get the local host name, needed for the user tx
                  String name = getTrimmedProperty ( AbstractUserTransactionServiceFactory.TM_UNIQUE_NAME_PROPERTY_NAME , p );
                  String url = getTrimmedProperty ( Context.PROVIDER_URL , p );
                   if ( url == null ) {
                      throw new SysException ( "Property not set: " + 
                      Context.PROVIDER_URL );
                  }
                  
                  String factory = getTrimmedProperty ( Context.INITIAL_CONTEXT_FACTORY , p );
                  if ( name == null || name.equals ( "" ) )
                      throw new SysException ( "Property not set: com.atomikos.icatch.tm_unique_name" );
                  UserTransactionServerImp utxs = 
                      UserTransactionServerImp.getSingleton();
                  utxs.init ( name , p );
                 
              }
             
				
			  //supercall will add resources
			  //and recover each one	
			  super.init ( info );
          }
          catch ( Exception e ) {
          	  //e.printStackTrace();
          	  Configuration.logWarning ( "Error in init(): " + e.getMessage() , e );
              Stack errors = new Stack();
              errors.push ( e );
              throw new SysException ( "Error in init(): " + e.getMessage()  , errors ); 
          }
      }
      
      public void shutdown ( boolean force ) 
      throws IllegalStateException
      { 
      	  TrmiTransactionManager tm = ( TrmiTransactionManager )
      	  	Configuration.getCompositeTransactionManager();
          if ( tm == null ) 
              return;
 
            tm.shutdown ( force );
            tm = null;
            
           
            
            try {
            	if ( lock_ != null ) {
            		lock_.release();
            		//lock_.channel().close();
            	}
            	if ( lockfilestream_ != null ) lockfilestream_.close();
            } catch (IOException e) {
            	// error release lock or shutting down channel
            	System.err.println ( "Error releasing file lock: "
            			+ e.getMessage());
            } finally {
            	lock_ = null;
            }
			
            if ( lockfile_ != null ) {
                lockfile_.delete ();
                lockfile_ = null;
            }

           
            //super call to shut down resources
            super.shutdown ( force );
      }
      
       /**
        *@see TSMetaData
        */
        
      public TSMetaData getTSMetaData()
      {
           
           return new TSMetaDataImp ( JTA.version , 
              VERSION , PRODUCT_NAME , 
              true , true );
      }
      
      /**
       *@see UserTransactionService
       */
       
       public UserTransaction getUserTransaction()
       {
           UserTransaction ret = null;
           ret = UserTransactionServerImp.getSingleton().getUserTransaction();
           if ( ret == null ) {
           		//not exported
           		ret = new J2eeUserTransaction();
           }
           
           return ret;
       }

       public TSInitInfo createTSInitInfo()
      {
           TSInitInfoImp ret = new TSInitInfoImp();
           ret.setProperties ( getDefaultProperties() );
           return ret;
      }

}
