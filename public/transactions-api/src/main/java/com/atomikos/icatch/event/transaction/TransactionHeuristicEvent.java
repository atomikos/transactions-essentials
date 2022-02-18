/**
 * Copyright (C) 2000-2022 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.event.transaction;

import com.atomikos.recovery.TxState;

 /**
  * Event to signal a heuristic outcome. 
  *
  */

public class TransactionHeuristicEvent extends TransactionEvent {
        
    public final String parentTransactionId;
    public final TxState state;
    
    public TransactionHeuristicEvent(String transactionId, String parentTransactionId, TxState state) {
        super(transactionId);
        this.parentTransactionId = parentTransactionId;
        this.state = state;
    }

    @Override
    public String toString() {
        StringBuffer ret = new StringBuffer();
        ret.append("Detected state: ").append(state).
            append(" for transaction ").append(transactionId);
            if (parentTransactionId != null) {
                ret.append(" with parent transaction ").append(parentTransactionId);
            }
            if (state.isHeuristic()) {
                ret.append(" (HINT: check https://www.atomikos.com/Documentation/HowToHandleHeuristics to learn more on how to handle heuristics...)");
            }
            
        return ret.toString();
    }

}
