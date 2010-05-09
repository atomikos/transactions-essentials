//$Id: HttpTransport.java,v 1.1.1.1 2006/10/02 15:21:15 guy Exp $
//$Log: HttpTransport.java,v $
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
//Revision 1.2  2006/03/15 10:31:48  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:12  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.4  2005/10/21 08:15:45  guy
//*** empty log message ***
//
//Revision 1.3  2005/10/10 08:09:48  guy
//Changed to allow null references for recovery coordinator
//(required by WS-AT: coordinator is not yet known when importing).
//
//Revision 1.2  2005/09/03 08:35:02  guy
//Added support for multiple protocols, and one HTTP configuration.
//
//Revision 1.1  2005/08/27 11:28:17  guy
//Added delegation model for sending (to avoid that the core
//classes would need the AXIS implementation classes in the global classpath)
//
package com.atomikos.icatch.msg;

import com.atomikos.icatch.system.Configuration;

/**
 * 
 * Copyright &copy; 2005, Atomikos. All rights reserved.
 * 
 * A transport for HTTP.
 * 
 * 
 */
public class HttpTransport extends AbstractTransport
{

    private static String createURL ( String ipAddress , int port ,
            String localPath , boolean secure )
    {
        String prefix = "http://";
        if ( secure )
            prefix = "https://";
        else
            prefix = "http://";
        return prefix + ipAddress + ":" + port + localPath;
    }

    private static int port = 8088;

    private static String serverIpAddress = null;

    private static boolean secureHttp = false;

    /**
     * Initializes the HttpTransport class; should be called before the first
     * instance is constructed.
     * 
     * @param serverIp
     * @param portNumber
     * @param useSecureHttp
     */
    public static void initialize ( String serverIp , int portNumber ,
            boolean useSecureHttp )
    {
        port = portNumber;
        serverIpAddress = serverIp;
        secureHttp = useSecureHttp;
    }

    public static int getPort ()
    {
        return port;
    }

    public static String getServerIpAddress ()
    {
        return serverIpAddress;
    }

    public static boolean useSecureHttp ()
    {
        return secureHttp;
    }

    /**
     * Creates a new instance.
     * 
     * @param name
     * @param participantLocalAddress
     * @param coordinatorLocalAddress
     * @param commitProtocol
     * @param transportProtocol
     * @param format
     * @param defaultTimeout
     */
    public HttpTransport ( String name , String participantLocalAddress ,
            String coordinatorLocalAddress , int commitProtocol ,
            int transportProtocol , int format , long defaultTimeout )
    {
        super ( name , createURL ( serverIpAddress, port,
                participantLocalAddress, secureHttp ) , createURL (
                serverIpAddress, port, coordinatorLocalAddress, secureHttp ) ,
                commitProtocol , transportProtocol , format , defaultTimeout );

        if ( serverIpAddress == null )
            throw new IllegalStateException ( "Please call initialize first" );
    }

    protected String createURL ( String localPath )
    {
        String ret = null;
        ret = createURL ( serverIpAddress, port, localPath, secureHttp );

        return ret;
    }

    public void requestReceived ( TransactionMessage msg )
    {
    		Configuration.logDebug ( "HttpTransport: received request message " + msg );
        super.requestReceived ( msg );
    }

    public void replyReceived ( TransactionMessage msg )
    {
        super.replyReceived ( msg );
    }

    /**
     * The context root of this transport.
     */
    public static final String CONTEXT_ROOT = "/atomikos";

}
