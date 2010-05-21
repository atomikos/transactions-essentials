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
