package com.atomikos.icatch.imp;

import com.atomikos.icatch.TxState;

/**
 * 
 * 
 * An active state handler.
 * 
 */

class TxActiveStateHandler extends TransactionStateHandler
{

    protected TxActiveStateHandler ( CompositeTransactionImp ct )
    {
        super ( ct );

    }

    protected Object getState ()
    {

        return TxState.ACTIVE;
    }

}
