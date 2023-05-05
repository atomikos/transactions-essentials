/**
 * Copyright (C) 2000-2023 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jms.internal;

import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

public class AtomikosTransactionRequiredJMSException extends AtomikosJMSException {
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LoggerFactory.createLogger(AtomikosTransactionRequiredJMSException.class);

    public static void throwAtomikosTransactionRequiredJMSException(String reason)
            throws AtomikosTransactionRequiredJMSException {
        LOGGER.logWarning(reason);
        throw new AtomikosTransactionRequiredJMSException(reason);
    }

    AtomikosTransactionRequiredJMSException(String reason) {
        super(reason);
    }

}
