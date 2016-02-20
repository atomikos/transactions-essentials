/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.jta;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.SubTxAwareParticipant;
import com.atomikos.icatch.config.Configuration;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.util.Assert;

/**
 * A subtx aware participant that resumes a previous 
 * transaction upon termination of the (sub)transaction
 * it is registered with. 
 *
 */
public class ResumePreviousTransactionSubTxAwareParticipant implements
        SubTxAwareParticipant
{

	private static final Logger LOGGER = LoggerFactory.createLogger(ResumePreviousTransactionSubTxAwareParticipant.class);

    private CompositeTransaction previous;
    
    public ResumePreviousTransactionSubTxAwareParticipant (
            CompositeTransaction previous )
    {
        Assert.notNull( "Previous transaction is null?", previous );
        this.previous = previous;
    }
    
    private void resume()
    {
        CompositeTransactionManager ctm = 
            Configuration.getCompositeTransactionManager();
        if ( ctm == null ) {
            LOGGER.logWarning ( "ResumePreviousTransactionSubTxAwareParticipant: no transaction manager found?" );
        } else {
            try {
                ctm.resume ( previous );
            }
            catch ( Exception error ) {
                LOGGER.logWarning ( "ResumePreviousTransactionSubTxAwareParticipant: could not resume previous transaction" , error );
            }
        }
        
    }
    
    public void committed ( CompositeTransaction tx )
    {
        resume();
    }

    public void rolledback ( CompositeTransaction tx )
    {
        resume();
    }

}
