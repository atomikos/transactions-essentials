package com.atomikos.icatch.msg;

/**
 * Copyright &copy; 2002, Atomikos. All rights reserved.
 * 
 * The commit protocols that can be supported by Atomikos.
 */

public interface CommitProtocol
{

    /**
     * Constant indicating an unknown commit protocol.
     */

    public static final int PROTOCOL_UNKNOWN = -1;

    /**
     * Constant indicating the BTP protocol.
     */

    public static final int PROTOCOL_BTP = 0;

    /**
     * Constant indicating the WS-T protocol.
     */

    public static final int PROTOCOL_WST = 1;

    /**
     * Constant indicating the native Atomikos portable propagation protocol.
     */

    public static final int PROTOCOL_APP = 2;

}
