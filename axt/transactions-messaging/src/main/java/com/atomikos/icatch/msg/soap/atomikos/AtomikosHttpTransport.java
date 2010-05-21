package com.atomikos.icatch.msg.soap.atomikos;

import com.atomikos.icatch.msg.CommitProtocol;
import com.atomikos.icatch.msg.HttpTransport;
import com.atomikos.icatch.msg.TransactionMessage;
import com.atomikos.icatch.msg.Transport;
import com.atomikos.icatch.system.Configuration;

public class AtomikosHttpTransport extends HttpTransport
{
	public static final String PARTICIPANT_RELATIVE_SERVICE_PATH = "services/atomikos/ParticipantPort";

	public static final String COORDINATOR_RELATIVE_SERVICE_PATH = "services/atomikos/CoordinatorPort";

	 
    /**
     * Local URL path of the participant service (starts with context root). The
     * complete URL is obtained by prepending the HTTP server's address.
     */
    public static final String PARTICIPANT_SERVICE_PATH = CONTEXT_ROOT
            + "/" + PARTICIPANT_RELATIVE_SERVICE_PATH;
    
   
    /**
     * Local URL path of the coordinator service (starts with context root). The
     * complete URL is obtained by prepending the HTTP server's address.
     */

    public static final String COORDINATOR_SERVICE_PATH = CONTEXT_ROOT
            + "/" + COORDINATOR_RELATIVE_SERVICE_PATH;

    private static HttpTransport singleton;

    /**
     * Initialize the transport. Overrides any previous initialization!
     * 
     * @param name
     * @param defaultTimeout
     */

    public static synchronized void init ( String name , long defaultTimeout )
    {

        Configuration.logDebug ( "initializing AtomikosHttpTransport" );
        singleton = new AtomikosHttpTransport ( name, defaultTimeout );
    }

    /**
     * Get the singleton instance.
     * 
     * @return The singleton, or null if not initialized.
     */
    public static HttpTransport getSingleton ()
    {
        Configuration.logDebug ( "returning AtomikosHttpTransport: "
                + singleton );
        return singleton;

    }

    // private TransactionServiceLocator locator;

    /**
     * Create a new transport.
     * 
     */
    private AtomikosHttpTransport ( String name , long timeout )
    {
        super ( name , PARTICIPANT_SERVICE_PATH , COORDINATOR_SERVICE_PATH ,
                CommitProtocol.PROTOCOL_APP , Transport.HTTP ,
                TransactionMessage.FORMAT_SOAP , timeout );
    }

}
