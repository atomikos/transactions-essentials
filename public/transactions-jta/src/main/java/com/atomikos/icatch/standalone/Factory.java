package com.atomikos.icatch.standalone;

import java.io.IOException;

import com.atomikos.persistence.LogStream;
import com.atomikos.persistence.StateRecoveryManager;
import com.atomikos.persistence.dataserializable.FileLogStream;
import com.atomikos.persistence.dataserializable.StateRecoveryManagerImp;
import com.atomikos.persistence.dataserializable.StreamObjectLog;
import com.atomikos.persistence.imp.VolatileStateRecoveryManager;

public class Factory {

	public static StateRecoveryManager createLogSystem(String logname, String logdir, boolean enableRecovery, long chckpt) throws IOException {
		//the default...
		//TODO : lookup...
		LogStream logstream = new FileLogStream ( logdir, logname );
        StreamObjectLog slog = new StreamObjectLog ( logstream, chckpt );

        StateRecoveryManager recmgr = null;
        if ( enableRecovery )
            recmgr = new StateRecoveryManagerImp ( slog );
        else
            recmgr = new VolatileStateRecoveryManager ();
		return recmgr;
	}
}
