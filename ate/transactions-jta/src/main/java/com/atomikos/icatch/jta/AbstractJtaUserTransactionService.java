package com.atomikos.icatch.jta;

import javax.transaction.TransactionManager;

import com.atomikos.datasource.xa.AcceptAllXATransactionalResource;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.imp.AbstractUserTransactionService;
import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;
import com.atomikos.icatch.system.Configuration;

public abstract class AbstractJtaUserTransactionService extends
		AbstractUserTransactionService 
{

	public AbstractJtaUserTransactionService() 
	{
		super();
	}

	public void init ( TSInitInfo info ) throws SysException 
	{
		
		super.init ( info );
        String autoRegisterProperty = getTrimmedProperty (
                AbstractUserTransactionServiceFactory.AUTOMATIC_RESOURCE_REGISTRATION_PROPERTY_NAME, info
                        .getProperties () );
        boolean autoRegister = "true".equals ( autoRegisterProperty );
        if ( Configuration.getResources ().hasMoreElements () && !autoRegister ) {
            AcceptAllXATransactionalResource defaultRes = new AcceptAllXATransactionalResource (
                    "com.atomikos.icatch.DefaultResource" );
            Configuration.addResource ( defaultRes );

        }
	}

	public void shutdown ( boolean force ) throws IllegalStateException 
	{
		super.shutdown(force);
        TransactionManagerImp.installTransactionManager ( null, false );
        UserTransactionServerImp.getSingleton ().shutdown ();
	}
	
    /**
     * @see UserTransactionService
     */

    public TransactionManager getTransactionManager ()
    {
        return com.atomikos.icatch.jta.TransactionManagerImp
                .getTransactionManager ();
    }



}
