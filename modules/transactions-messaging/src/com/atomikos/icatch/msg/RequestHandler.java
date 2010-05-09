//$Id: RequestHandler.java,v 1.1.1.1 2006/10/02 15:21:15 guy Exp $
//$Log: RequestHandler.java,v $
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
//Revision 1.2  2006/03/15 10:31:49  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:12  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.5  2005/11/01 09:10:21  guy
//Improved/debugged during testing.
//
//Revision 1.4  2005/10/03 11:46:03  guy
//Added support for WS-T registration (2PC and volatile).
//
//Revision 1.3  2005/08/13 13:41:35  guy
//Added test feedback and some logging.
//
//Revision 1.2  2005/08/05 15:03:47  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.1.2.1  2005/07/25 14:00:02  guy
//Added thread pooling for incoming message requests.
//
package com.atomikos.icatch.msg;

import com.atomikos.icatch.system.Configuration;

/**
 * 
 * Copyright &copy; 2005, Atomikos. All rights reserved.
 * 
 * 
 * A handler for the handling of incoming requests.
 * 
 */

class RequestHandler implements Runnable
{

    private CommitServer server;
    private TransactionMessage msg;
    private Transport transport;

    RequestHandler ( CommitServer server , TransactionMessage msg , Transport transport )
    {
        this.server = server;
        this.msg = msg;
        this.transport = transport;
    }

    public void run ()
    {
        try {
            // determine the type of the message
            // and act accordingly
            if ( msg instanceof PrepareMessage ) {
                server.processPrepare ( (PrepareMessage) msg, transport );
            } else if ( msg instanceof CommitMessage ) {
                server.processCommit ( (CommitMessage) msg, transport );
            } else if ( msg instanceof RollbackMessage ) {
                server.processRollback ( (RollbackMessage) msg, transport );
            } else if ( msg instanceof ForgetMessage ) {
                server.processForget ( (ForgetMessage) msg, transport );
            } else if ( msg instanceof ReplayMessage ) {
                server.processReplay ( (ReplayMessage) msg, transport );
            } else if ( msg instanceof RegisterMessage ) {
                server.processRegister ( (RegisterMessage) msg, transport );
            } else if ( msg instanceof RegisteredMessage ) {
                // ignore: we don't really need this?!
            } else {
                // message not understood
                Configuration.logWarning ( "Incoming message not understood: "
                        + msg );
            }

        } catch ( Exception e ) {
            // e.printStackTrace();
            // no exception should be returned during notification!
            Configuration.logWarning ( "Error in message receival for msg: "
                    + msg, e );
        }
    }


}
