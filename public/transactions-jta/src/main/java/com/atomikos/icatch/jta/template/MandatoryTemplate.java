/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.jta.template;

import java.util.concurrent.Callable;

import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

class MandatoryTemplate extends TransactionTemplate {

    public MandatoryTemplate(TransactionManager utm, int timeout) {
        super(utm, timeout);
    }
    
    public <T> T execute(Callable<T> work) throws Exception {
        Transaction existingTransaction = utm.getTransaction();
        if (existingTransaction == null) {
            throw new IllegalStateException("A transaction is required but none exists.");
        }
        return super.execute(work);
    }

}
