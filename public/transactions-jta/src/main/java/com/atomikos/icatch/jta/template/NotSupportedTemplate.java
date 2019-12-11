/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.jta.template;

import java.util.concurrent.Callable;

import javax.transaction.TransactionManager;

class NotSupportedTemplate extends TransactionTemplate {

    public NotSupportedTemplate(TransactionManager utm, int timeout) {
        super(utm, timeout);
    }

    public <T> T execute(Callable<T> work) throws Exception {
        T ret = null;
        try {
            suspendExistingTransaction();
            ret = work.call();
        } finally {
            resumeExistingTransaction();
        }
        return ret;
    }
}
