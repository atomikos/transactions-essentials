package com.atomikos.icatch.trmi;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;

import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RecoveryCoordinator;

/**
 *Copyright &copy; 2001, Atomikos. All rights reserved.
 *
 *A proxy for network wide recovery.
 */

class RecoveryCoordinatorProxy implements RecoveryCoordinator,
				         java.io.Serializable
{
    private String root_ = null;

    private String servername_ = null;
    //contains RMI name of server! for persistence!
    
    private String providerUrl_;
    
    private String initialContextFactory_;
    
    public RecoveryCoordinatorProxy ( 
    String root , String servername , String initialContextFactory , 
    String providerUrl ) 
    {
        servername_ = servername;
        root_ = root;
        initialContextFactory_ = initialContextFactory;
        providerUrl_ = providerUrl;
    }
    
    
    
    public Boolean replayCompletion ( Participant participant )
    {
        RecoveryServer server = null;
        Boolean ret = null;

        try {
                Hashtable env = new Hashtable();
                env.put ( Context.INITIAL_CONTEXT_FACTORY , 
                    initialContextFactory_ );
                env.put ( Context.PROVIDER_URL , providerUrl_ );
                Context ctx = new InitialContext ( env );
	  server = ( RecoveryServer )
              PortableRemoteObject.narrow ( ctx.lookup ( servername_ ) , RecoveryServer.class );
	  ret = server.replayCompletion ( root_ , participant );
        }
        catch ( Exception e ) {
	  
        }

        return ret;
    }
    
    public String getURI()
    {
        return servername_ + "?" + root_; 
    }
}
