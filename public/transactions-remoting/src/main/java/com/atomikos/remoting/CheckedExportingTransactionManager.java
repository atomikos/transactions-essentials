/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.remoting;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.ExportingTransactionManager;
import com.atomikos.icatch.Extent;
import com.atomikos.icatch.Propagation;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.Synchronization;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.config.Configuration;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.recovery.TxState;

 /**
  * Wrapper implementation that decorates another implementation with checking:
  * at commit time, this instance makes sure that there are no outstanding calls.
  * 
  * The original motivation for adding this functionality was due to HTTP 202 
  * response codes: in such cases, our response filters are not invoked at all
  * and the transaction will commit without extent for the remote work - 
  * which in turn means an unclear commit scope (since what your application commits
  * is not what it may think it commits: the pending work of the remote 202 response is not part of the commit).
  *
  */
public class CheckedExportingTransactionManager implements ExportingTransactionManager {
    
    private static final Logger LOGGER = LoggerFactory.createLogger(CheckedExportingTransactionManager.class);
    
    private static Map<String,PendingRequestSynchronisation> pendingRequestSynchronisation = new ConcurrentHashMap<String,PendingRequestSynchronisation>();

    private ExportingTransactionManager delegate;
    
    public CheckedExportingTransactionManager(ExportingTransactionManager delegate) {
        this.delegate = delegate;
    }
    
    @Override
    public Propagation getPropagation() throws SysException, IllegalStateException {
        CompositeTransactionManager ctm = Configuration.getCompositeTransactionManager();
        CompositeTransaction ct = ctm.getCompositeTransaction();
        if (ct == null) {
            throw new IllegalStateException("This method requires a transaction but none was found");
        }
        registerPendingRequestSynchronisation(ct);
        return delegate.getPropagation();
    }

    @Override
    public void addExtent(Extent extent) throws SysException, IllegalArgumentException, RollbackException {
        delegate.addExtent(extent);
        markRequestAsCompleted(extent.getParentTransactionId());
    }
    
    private void registerPendingRequestSynchronisation(CompositeTransaction ct) {
        PendingRequestSynchronisation s = new PendingRequestSynchronisation(ct);
        pendingRequestSynchronisation.put(ct.getTid(), s);
        ct.registerSynchronization(s);
    }
    

    private static void markRequestAsCompleted(String tid) {
        PendingRequestSynchronisation s = pendingRequestSynchronisation.get(tid);
        if (s != null) {
            s.markAsDone();
            pendingRequestSynchronisation.remove(tid);
        }
    }
    
    private static class PendingRequestSynchronisation implements Synchronization {

        private boolean done;
        private CompositeTransaction ct;

        PendingRequestSynchronisation(CompositeTransaction ct) {
            this.ct = ct;
        }

        private void markAsDone() {
            done = true;
        }

        @Override
        public void beforeCompletion() {
            if (!done) {
                LOGGER.logWarning(
                        "Pending outgoing remote request detected at transaction commit - forcing rollback since commit scope will not be as expected!\n"
                                + "Possible causes: attempting to commit a transaction with timed out remote calls, calling a remote service that returns HTTP 202 Accepted or an invalid extent in the return...");
                ct.setRollbackOnly();
            }
        }

        @Override
        public void afterCompletion(TxState txstate) {
            markRequestAsCompleted(ct.getTid());
        }

    }



}
