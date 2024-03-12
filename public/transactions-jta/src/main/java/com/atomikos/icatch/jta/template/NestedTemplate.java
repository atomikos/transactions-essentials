/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.jta.template;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.concurrent.Callable;

import javax.transaction.TransactionManager;

class NestedTemplate extends TransactionTemplate {

    protected NestedTemplate(TransactionManager utm, int timeout) {
        super(utm, timeout);
    }
    
    public <T> T execute(Callable<T> work) throws Exception {
        T ret = null;
        try {
        	beginTransaction();
            ret = work.call();
            utm.commit();
        } catch (Exception e) {
            utm.rollback();
            throw e;
        } catch (Throwable e) {
            utm.rollback();
            throw new UndeclaredThrowableException(e);
        }
        return ret;
    }
    
}
