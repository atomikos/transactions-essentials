/**
 * Copyright (C) 2000-2022 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.imp;

import com.atomikos.recovery.TxState;

/**
 * An active state handler.
 */

class TxActiveStateHandler extends TransactionStateHandler
{

    protected TxActiveStateHandler ( CompositeTransactionImp ct )
    {
        super ( ct );

    }

    protected TxState getState ()
    {

        return TxState.ACTIVE;
    }

}
