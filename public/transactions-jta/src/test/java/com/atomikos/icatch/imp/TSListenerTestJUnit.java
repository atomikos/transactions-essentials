package com.atomikos.icatch.imp;

import java.util.Properties;

import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;

public class TSListenerTestJUnit 
extends AbstractJUnitTSListenerTest 
{

	public TSListenerTestJUnit(String name) 
	{
		super(name);
	}

	protected UserTransactionService init() 
	{
		UserTransactionServiceImp uts = new UserTransactionServiceImp ();

        TSInitInfo info = uts.createTSInitInfo ();
        Properties properties = info.getProperties ();
        properties.setProperty ( AbstractUserTransactionServiceFactory.TM_UNIQUE_NAME_PROPERTY_NAME,
                "TSListenerTestTransactionManager" );
        properties.setProperty ( AbstractUserTransactionServiceFactory.OUTPUT_DIR_PROPERTY_NAME,
                getTemporaryOutputDir () );
        properties.setProperty ( AbstractUserTransactionServiceFactory.LOG_BASE_DIR_PROPERTY_NAME,
                getTemporaryOutputDir () );
        properties.setProperty ( AbstractUserTransactionServiceFactory.CONSOLE_LOG_LEVEL_PROPERTY_NAME,
                "DEBUG" );
        properties.setProperty ( AbstractUserTransactionServiceFactory.MAX_ACTIVES_PROPERTY_NAME, "25000" );
        return uts;
	}

}
