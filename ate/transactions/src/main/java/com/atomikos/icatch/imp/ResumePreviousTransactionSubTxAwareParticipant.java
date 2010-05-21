package com.atomikos.icatch.imp;

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
            Configuration.logWarning ( "ResumePreviousTransactionSubTxAwareParticipant: no transaction manager found?" );
        } 
        else {
            try {
                ctm.resume ( previous );
            }
            catch ( Exception error ) {
                Configuration.logWarning ( "ResumePreviousTransactionSubTxAwareParticipant: could not resume previous transaction" , error );
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
