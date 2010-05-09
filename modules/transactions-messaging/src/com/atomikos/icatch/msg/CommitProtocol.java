//$Id: CommitProtocol.java,v 1.1.1.1 2006/10/02 15:21:14 guy Exp $
//$Log: CommitProtocol.java,v $
//Revision 1.1.1.1  2006/10/02 15:21:14  guy
//Import into CVS.
//
//Revision 1.1.1.1  2006/04/29 08:55:46  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:29  guy
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
//Revision 1.1  2005/08/23 13:06:36  guy
//Updated SOAP init parameters.
//Moved CommitProtocol to msg package.
//
//Revision 1.2  2005/08/05 15:03:27  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.1.4.1  2004/06/14 08:09:08  guy
//Merged redesign2002 with redesign2003.
//
//Revision 1.1.2.1  2002/11/13 17:46:47  guy
//Redesigned to make the commit protocol explicit in the core.
//

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
