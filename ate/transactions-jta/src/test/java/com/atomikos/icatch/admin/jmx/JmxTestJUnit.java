package com.atomikos.icatch.admin.jmx;

import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;

public class JmxTestJUnit extends AbstractJUnitTest {

	public JmxTestJUnit(String name) 
	{
		super(name);
	}

	protected UserTransactionService startUp() 
	{
		UserTransactionServiceImp uts = new UserTransactionServiceImp();
		TSInitInfo info = uts.createTSInitInfo();
		info.registerLogAdministrator( JmxLogAdministrator.getInstance() );
		//set a test-specific log name to avoid heuristics from 
		//other tests in the logs
		java.util.Properties p = info.getProperties();
		p.setProperty ( AbstractUserTransactionServiceFactory.LOG_BASE_NAME_PROPERTY_NAME , "TestJMXLog");
		p.setProperty ( AbstractUserTransactionServiceFactory.OUTPUT_DIR_PROPERTY_NAME , getTemporaryOutputDir() );
    		p.setProperty ( AbstractUserTransactionServiceFactory.LOG_BASE_DIR_PROPERTY_NAME , getTemporaryOutputDir() );
    		p.setProperty ( AbstractUserTransactionServiceFactory.CONSOLE_LOG_LEVEL_PROPERTY_NAME , "DEBUG" );
		uts.init ( info );
		return uts;
	}

}
