//$Id: RecoveryCoordinatorProxy.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//$Log: RecoveryCoordinatorProxy.java,v $
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
//Revision 1.1.1.1  2006/03/09 14:59:32  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.7  2005/08/09 15:24:13  guy
//Updated javadoc, and redesigned CompositeTransaction interface
//(eliminated TransactionControl and CompositeTerminator).
//
//Revision 1.6  2005/08/05 15:04:23  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.5  2004/10/12 13:03:53  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.4  2003/08/27 06:24:07  guy
//Adapted to RMI-IIOP.
//
//$Id: RecoveryCoordinatorProxy.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Revision 1.3  2003/03/11 06:39:16  guy
//$Id: RecoveryCoordinatorProxy.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Merged in changes from transactionsJTA100 branch.
//$Id: RecoveryCoordinatorProxy.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//
//Revision 1.2.4.1  2003/01/29 17:20:07  guy
//Adapted to use JNDI binding instead of Naming of RMI.
//
//Revision 1.2  2001/10/29 16:38:11  guy
//Changed UniqueId for String.
//
//Revision 1.1.1.1  2001/10/09 12:37:26  guy
//Core module
//
//Revision 1.1  2001/03/26 16:01:27  pardon
//Updated Proxy to use serial for SubTxAware notification.
//

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
