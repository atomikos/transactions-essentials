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
