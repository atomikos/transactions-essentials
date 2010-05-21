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
