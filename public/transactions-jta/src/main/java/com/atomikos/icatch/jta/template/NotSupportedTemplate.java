/**
 * Copyright (C) 2000-2023 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.jta.template;

import java.util.concurrent.Callable;

import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

class NotSupportedTemplate extends TransactionTemplate {

    public NotSupportedTemplate(TransactionManager utm, int timeout) {
        super(utm, timeout);
    }

    public <T> T execute(Callable<T> work) throws Exception {
        T ret = null;
        Transaction tx = null;
        try {
            tx = utm.suspend();
            ret = work.call();
        } finally {
            if (tx != null) {
            	utm.resume(tx);
            }
        }
        return ret;
    }
}
