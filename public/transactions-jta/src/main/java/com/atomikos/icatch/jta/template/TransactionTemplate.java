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

import com.atomikos.icatch.jta.UserTransactionManager;

 /**
  * Template (builder-style) logic for light-weight transaction demarcation.
  * 
  * Example usage:
  * 
  * <pre>
  * {@code
  *     TransactionTemplate template = new TransactionTemplate();
  *     template.withTimeout(5).required().execute(() -> {
  *         //your transactional code as a lambda expression
  *     });
  *  }
  * </pre>
  * 
  * Instead of a lambda expression, you can also supply a Callable instance.
  */

public class TransactionTemplate {
    
    private int timeout; //0 means: use default
    protected TransactionManager utm;
    
    
    public TransactionTemplate() {
        this(new UserTransactionManager(), 0);
    }
    
    TransactionTemplate(TransactionManager utm, int timeout) {
        this.timeout = timeout;
        this.utm = utm;
    }
    
    public TransactionTemplate withTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }
    
    /**
     * 
     * @return An instance that executes work with REQUIRED semantics.
     * 
     * <ul>
     * <li>For new transactions: any exception will lead to rollback, no exception means commit.</li>
     * <li>For existing transactions: any exception will mark the transaction as rollbackOnly.</li>
     * </ul>
     * 
     */
    public TransactionTemplate required() {
        return new RequiredTemplate(utm, timeout);
    }
    
    /**
     * @return An instance that executes work in a new (nested) transaction and commits if there are no exceptions.
     * 
     * Any exception will lead to rollback of the (nested) transaction.
     * 
     */
    public TransactionTemplate nested() {
        return new NestedTemplate(utm, timeout);
    }
    
    protected Transaction beginTransaction() throws Exception {
    	utm.setTransactionTimeout(timeout);
    	utm.begin();
    	return utm.getTransaction();
    }
    
    /**
     * Defaults to required() strategy.
     * 
     * @param work
     * @return
     * @throws Exception
     */
    public <T> T execute(Callable<T> work) throws Exception {
       return new RequiredTemplate(utm, timeout).execute(work);
    }
    
    /**
     * 
     * @return An instance that executes work with REQUIRES_NEW semantics.
     * 
     * Any exception will lead to rollback, no exception means commit.
     */
    
    public TransactionTemplate requiresNew() {
        return new RequiresNewTemplate(utm, timeout);
    }

    /**
     * 
     * @return An instance that executes work with MANDATORY semantics.
     * 
     * Any exception will lead to rollbackOnly status of the existing transaction.
     */
    public TransactionTemplate mandatory() {
        return new MandatoryTemplate(utm, timeout);
    }

    /**
     * 
     * @return An instance that executes work with NEVER semantics.
     */
    
    public TransactionTemplate never() {
        return new NeverTemplate(utm, timeout);
    }

    /**
     * 
     * @return An instance that executes work with SUPPORTS semantics.
     */
    
    public TransactionTemplate supports() {
        return new SupportsTemplate(utm, timeout);
    }

    /**
     * 
     * @return An instance that executes work with NOT_SUPPORTED semantics.
     */
    
    public TransactionTemplate notSupported() {
        return new NotSupportedTemplate(utm, timeout);
    }

}
