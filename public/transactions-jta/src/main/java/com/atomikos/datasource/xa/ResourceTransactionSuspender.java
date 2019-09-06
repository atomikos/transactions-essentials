/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.datasource.xa;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.SubTxAwareParticipant;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

class ResourceTransactionSuspender implements SubTxAwareParticipant {

    private static Logger LOGGER = LoggerFactory.createLogger(ResourceTransactionSuspender.class);
    
    private XAResourceTransaction branch;
    
    ResourceTransactionSuspender(XAResourceTransaction branch) {
        this.branch = branch;
    }
    
    @Override
    public void committed(CompositeTransaction transaction) {
        try {
            branch.suspend();
        } catch (Exception e) {
            LOGGER.logDebug("Unexpected exception while trying to suspend the branch: ", e);
            //ignore: just a courtesy
        }
    }

    @Override
    public void rolledback(CompositeTransaction transaction) {
        try {
            branch.suspend();
        } catch (Exception e) {
            LOGGER.logDebug("Unexpected exception while trying to suspend the branch: ", e);
            //ignore: just a courtesy
        }
    }

}
