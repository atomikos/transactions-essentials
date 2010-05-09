//$Id: WsatHttpTransport.java,v 1.1.1.1 2006/10/02 15:21:15 guy Exp $
//$Log: WsatHttpTransport.java,v $
//Revision 1.1.1.1  2006/10/02 15:21:15  guy
//Import into CVS.
//
//Revision 1.1.1.1  2006/04/29 08:55:46  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:30  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:28  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:54  guy
//Import.
//
//Revision 1.2  2006/03/15 10:31:52  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:13  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.3  2005/11/07 10:57:22  guy
//Adapted after testing.
//
//Revision 1.2  2005/10/21 08:17:37  guy
//Added method to get registration service address.
//
//Revision 1.1  2005/10/10 08:10:56  guy
//Added WSAT transport.
//
package com.atomikos.icatch.msg.soap.wsat;

import com.atomikos.icatch.msg.CommitProtocol;
import com.atomikos.icatch.msg.HttpTransport;
import com.atomikos.icatch.msg.TransactionMessage;
import com.atomikos.icatch.msg.Transport;
import com.atomikos.icatch.system.Configuration;

/**
 * 
 * Copyright &copy; 2005, Atomikos. All rights reserved.
 * 
 * 
 * 
 * 
 */
public class WsatHttpTransport extends HttpTransport
{

    /**
     * Local URL path of the participant service (starts with context root). The
     * complete URL is obtained by prepending the HTTP server's address.
     */
    public static final String PARTICIPANT_SERVICE_PATH = CONTEXT_ROOT
            + "/services/wsat/ParticipantPort";

    /**
     * Local URL path of the coordinator service (starts with context root). The
     * complete URL is obtained by prepending the HTTP server's address.
     */

    public static final String COORDINATOR_SERVICE_PATH = CONTEXT_ROOT
            + "/services/wsat/CoordinatorPort";

    /**
     * Local URL path of the registration service (starts with context root).
     * The complete URL is obtained by prepending the HTTP server's address.
     */
    public static final String REGISTRATION_SERVICE_PATH = CONTEXT_ROOT
            + "/services/wscoor/RegistrationCoordinatorPort";

    /**
     * Local URL path of the WSAT fault service (starts with context root). The
     * complete URL is obtained by prepending the HTTP server's address.
     */

    public static final String WSAT_FAULT_SERVICE_PATH = CONTEXT_ROOT
            + "/services/wsat/FaultPort";

    /**
     * Local URL path of the WSC fault service (starts with context root). The
     * complete URL is obtained by prepending the HTTP server's address.
     */
    public static final String WSC_FAULT_SERVICE_PATH = CONTEXT_ROOT
            + "/services/wscoor/FaultPort";

    /**
     * Local URL path of the registration requestor service (starts with context
     * root). The complete URL is obtained by prepending the HTTP server's
     * address.
     */
    public static final String REG_REQUESTER_SERVICE_PATH = CONTEXT_ROOT
            + "/services/wscoor/RegistrationRequesterPort";

    private static HttpTransport singleton;

    /**
     * Initialize the transport. Overrides any previous initialization!
     * 
     * @param name
     * @param defaultTimeout
     */

    public static synchronized void init ( String name , long defaultTimeout )
    {

        Configuration.logDebug ( "initializing WsatHttpTransport" );
        singleton = new WsatHttpTransport ( name, defaultTimeout );
    }

    /**
     * Get the singleton instance.
     * 
     * @return The singleton, or null if not initialized.
     */
    public static HttpTransport getSingleton ()
    {
        Configuration.logDebug ( "returning WsatHttpTransport: " + singleton );
        return singleton;

    }

    /**
     * Create a new transport.
     * 
     */
    private WsatHttpTransport ( String name , long timeout )
    {
        super ( name , PARTICIPANT_SERVICE_PATH , COORDINATOR_SERVICE_PATH ,
                CommitProtocol.PROTOCOL_APP , Transport.HTTP ,
                TransactionMessage.FORMAT_SOAP , timeout );
    }

    public String getRegistrationServiceURL ()
    {
        return createURL ( REGISTRATION_SERVICE_PATH );
    }

    public String getWsatFaultServiceURL ()
    {
        return createURL ( WSAT_FAULT_SERVICE_PATH );
    }

    public String getWscFaultServiceURL ()
    {
        return createURL ( WSC_FAULT_SERVICE_PATH );
    }

    public String getRegistrationRequesterServiceURL ()
    {
        return createURL ( REG_REQUESTER_SERVICE_PATH );
    }

}
