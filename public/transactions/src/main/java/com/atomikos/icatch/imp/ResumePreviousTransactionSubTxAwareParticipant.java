/**
 * Copyright (C) 2000-2010 Atomikos <info@atomikos.com>
 *
 * This code ("Atomikos TransactionsEssentials"), by itself,
 * is being distributed under the
 * Apache License, Version 2.0 ("License"), a copy of which may be found at
 * http://www.atomikos.com/licenses/apache-license-2.0.txt .
 * You may not use this file except in compliance with the License.
 *
 * While the License grants certain patent license rights,
 * those patent license rights only extend to the use of
 * Atomikos TransactionsEssentials by itself.
 *
 * This code (Atomikos TransactionsEssentials) contains certain interfaces
 * in package (namespace) com.atomikos.icatch
 * (including com.atomikos.icatch.Participant) which, if implemented, may
 * infringe one or more patents held by Atomikos.
 * It should be appreciated that you may NOT implement such interfaces;
 * licensing to implement these interfaces must be obtained separately from Atomikos.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package com.atomikos.icatch.imp;

import com.atomikos.logging.LoggerFactory;
import com.atomikos.logging.Logger;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.SubTxAwareParticipant;
import com.atomikos.icatch.system.Configuration;

/**
 * 
 * 
 *
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
        if ( previous == null ) 
            throw new IllegalArgumentException ( "Previous transaction is null?" );
        this.previous = previous;
    }
    
    private void resume()
    {
        CompositeTransactionManager ctm = 
            Configuration.getCompositeTransactionManager();
        if ( ctm == null ) {
            LOGGER.logWarning ( "ResumePreviousTransactionSubTxAwareParticipant: no transaction manager found?" );
        } 
        else {
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
