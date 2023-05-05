/**
 * Copyright (C) 2000-2023 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.jta.template;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.concurrent.Callable;

import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

class RequiredTemplate extends TransactionTemplate {
    
    protected RequiredTemplate(TransactionManager utm, int timeout) {
        super(utm, timeout);
    }
    
    public <T> T execute(Callable<T> work) throws Exception {
        T ret = null;
        boolean transactionStartedHere = false;
        try {
            if (utm.getTransaction() == null) {
            	beginTransaction();
            	transactionStartedHere = true;
            }
            ret = work.call();
            if (transactionStartedHere) {
            	utm.commit();
            }
        } catch (Exception e) {
            handleException(transactionStartedHere);
            throw e;
        } catch (Throwable e) {
        	handleException(transactionStartedHere);
            throw new UndeclaredThrowableException(e);
        }
        return ret;
    }

	private void handleException(boolean transactionStartedHere) throws SystemException {
		if (transactionStartedHere) {
			utm.rollback();
		} else {
			utm.setRollbackOnly();
		}
	}
}
