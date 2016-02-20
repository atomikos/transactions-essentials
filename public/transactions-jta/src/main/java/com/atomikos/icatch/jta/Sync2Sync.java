/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.jta;

import javax.transaction.Status;

import com.atomikos.icatch.TxState;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

class Sync2Sync implements com.atomikos.icatch.Synchronization
{

	private static final long serialVersionUID = 2217827831174006366L;

	private static final Logger LOGGER = LoggerFactory.createLogger(Sync2Sync.class);

    private javax.transaction.Synchronization sync;

    private Boolean committed; //null for readonly
    
    Sync2Sync ( javax.transaction.Synchronization sync )
    {
        this.sync = sync;
    }

    @Override
	public void beforeCompletion ()
    {
        this.sync.beforeCompletion ();
        resetForReuse();
        LOGGER.logInfo("beforeCompletion() called on Synchronization: " + this.sync.toString());
    }

	private void resetForReuse() {
        this.committed = null;
	}

    @Override
	public void afterCompletion ( TxState state )
    {
        if ( state == TxState.TERMINATED ) {
            if ( this.committed == null ) { //readonly: unknown
                this.sync.afterCompletion ( Status.STATUS_UNKNOWN );
                LOGGER.logInfo ( "afterCompletion ( STATUS_UNKNOWN ) called "
                                + " on Synchronization: " + this.sync.toString () );
            } else {
                boolean commit = this.committed.booleanValue ();
                if ( commit ) {
                    this.sync.afterCompletion ( Status.STATUS_COMMITTED );
                    LOGGER.logInfo ( "afterCompletion ( STATUS_COMMITTED ) called "
                                    + " on Synchronization: "
                                    + this.sync.toString () );
                } else {
                    this.sync.afterCompletion ( Status.STATUS_ROLLEDBACK );
                    LOGGER.logInfo ( "afterCompletion ( STATUS_ROLLEDBACK ) called "
                                    + " on Synchronization: "
                                    + this.sync.toString () );
                }
            }
        } else if ( state == TxState.COMMITTING ) this.committed = Boolean.TRUE;
          else if ( state == TxState.ABORTING ) this.committed = Boolean.FALSE;

    }
}
