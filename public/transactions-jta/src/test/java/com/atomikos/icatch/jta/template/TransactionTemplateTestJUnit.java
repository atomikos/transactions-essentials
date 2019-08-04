/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.jta.template;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class TransactionTemplateTestJUnit {

    private TransactionTemplate template;
    private TransactionManager mockedTm;
    private TransactionTemplate recursiveTemplate;
    
    @Before
    public void setUp() throws Exception {
        mockedTm = Mockito.mock(TransactionManager.class);
        template = new TransactionTemplate(mockedTm, 0);       
    }

    @Test
    public void testRequiredPerformsCommitIfNoException() throws Exception {
        template.required().execute(() -> {return null;});
        Mockito.verify(mockedTm).commit();
        Mockito.verify(mockedTm, Mockito.never()).rollback();
        Mockito.verify(mockedTm, Mockito.never()).setRollbackOnly();
    }
    
    @Test
    public void testRequiredPerformsRollbackOnException() throws Exception {
        try {
            template.required().execute(() -> {throw new Exception();});
        } catch (Exception ok) {}
        Mockito.verify(mockedTm).rollback();
        Mockito.verify(mockedTm, Mockito.never()).commit();
    }
    
    @Test
    public void testRequiredWithExistingTransactionCallsRollbackOnlyForException() throws Exception {
        Transaction mockedTransaction = Mockito.mock(Transaction.class);
        Mockito.when(mockedTm.getTransaction()).thenReturn(mockedTransaction);
        try {
            template.required().execute(() -> {throw new Exception();});
        } catch (Exception ok) {}
        Mockito.verify(mockedTm).setRollbackOnly();
        Mockito.verify(mockedTm, Mockito.never()).rollback();
    }

    @Test
    public void testRequiredWithExistingTransactionDoesNotCommit() throws Exception {
        Transaction mockedTransaction = Mockito.mock(Transaction.class);
        Mockito.when(mockedTm.getTransaction()).thenReturn(mockedTransaction);
        try {
            template.required().execute(() -> {return null;});
        } catch (Exception ok) {}
        Mockito.verify(mockedTm, Mockito.never()).commit();
    }

    @Test
    public void testNestedCommitsSubtransaction() throws Exception {
        Transaction mockedTransaction = Mockito.mock(Transaction.class);
        Mockito.when(mockedTm.getTransaction()).thenReturn(mockedTransaction);
        try {
            template.nested().execute(() -> {return null;});
        } catch (Exception ok) {}
        Mockito.verify(mockedTm).commit();
        Mockito.verify(mockedTm, Mockito.never()).rollback();
        Mockito.verify(mockedTm, Mockito.never()).setRollbackOnly();
    }
    
    @Test
    public void testNestedRollsbackSubtransactionOnException() throws Exception {
        Transaction mockedTransaction = Mockito.mock(Transaction.class);
        Mockito.when(mockedTm.getTransaction()).thenReturn(mockedTransaction);
        try {
            template.nested().execute(() -> {throw new Exception();});
        } catch (Exception ok) {}
        Mockito.verify(mockedTm).rollback();
        Mockito.verify(mockedTm, Mockito.never()).commit();
    }
 
    @Test
    public void testRequiresNewSuspendsExistingTransaction() throws Exception {
        Transaction mockedTransaction = Mockito.mock(Transaction.class);
        Mockito.when(mockedTm.getTransaction()).thenReturn(mockedTransaction);
        Mockito.when(mockedTm.suspend()).thenReturn(mockedTransaction);
        template.requiresNew().execute(() -> {return null;});
        Mockito.verify(mockedTm).suspend();
        Mockito.verify(mockedTm).begin();
        Mockito.verify(mockedTm).commit();
        Mockito.verify(mockedTm).resume(Mockito.any());
    }
    
    @Test(expected=IllegalStateException.class)
    public void testMandatoryThrowsWithoutExistingTransaction() throws Exception {
        template.mandatory().execute(() -> {return null;});
    }
    
    @Test
    public void testMandatory() throws Exception {
        Transaction mockedTransaction = Mockito.mock(Transaction.class);
        Mockito.when(mockedTm.getTransaction()).thenReturn(mockedTransaction);
        template.mandatory().execute(() -> {return null;});
        Mockito.verify(mockedTm, Mockito.never()).begin();
        Mockito.verify(mockedTm, Mockito.never()).commit();
    }
    
    @Test(expected=IllegalStateException.class)
    public void testNeverThrowsWithExistingTransaction() throws Exception {
        Transaction mockedTransaction = Mockito.mock(Transaction.class);
        Mockito.when(mockedTm.getTransaction()).thenReturn(mockedTransaction);
        template.never().execute(() -> {return null;});
    }
    
    @Test
    public void testNeverDoesNotStartTransaction() throws Exception {
        template.never().execute(() -> {return null;});
        Mockito.verify(mockedTm, Mockito.never()).begin();
    }
    
    @Test
    public void testSupportsReusesExistingTransaction() throws Exception {
        Transaction mockedTransaction = Mockito.mock(Transaction.class);
        Mockito.when(mockedTm.getTransaction()).thenReturn(mockedTransaction);
        template.supports().execute(() -> {return null;});
        Mockito.verify(mockedTm, Mockito.never()).begin();
        Mockito.verify(mockedTm, Mockito.never()).suspend();
        Mockito.verify(mockedTm, Mockito.never()).commit();
        Mockito.verify(mockedTm, Mockito.never()).rollback();
    }
    
    @Test
    public void testSupportsDoesNotStartTransaction() throws Exception {
        template.supports().execute(() -> {return null;});
        Mockito.verify(mockedTm, Mockito.never()).begin();
    }
    
    @Test
    public void testNotSupportedSuspendsExistingTransaction() throws Exception {
        Transaction mockedTransaction = Mockito.mock(Transaction.class);
        Mockito.when(mockedTm.getTransaction()).thenReturn(mockedTransaction);
        Mockito.when(mockedTm.suspend()).thenReturn(mockedTransaction);
        template.notSupported().execute(() -> {return null;});
        Mockito.verify(mockedTm).suspend();
        Mockito.verify(mockedTm).resume(Mockito.any());
        Mockito.verify(mockedTm, Mockito.never()).begin();
        Mockito.verify(mockedTm, Mockito.never()).commit();
        Mockito.verify(mockedTm, Mockito.never()).rollback();
    }
    
    @Test 
    public void testNotSupported() throws Exception {
        template.notSupported().execute(() -> {return null;});
        Mockito.verify(mockedTm, Mockito.never()).begin();
    }
    
    @Test
    public void testCallingWithTimeoutDoesNotChangeStrategy() {
        TransactionTemplate required = template.required();
        assertSame(required, required.withTimeout(5));
    }
    
    @Test
    public void testRecursiveTransactionScopesAreIsolatedFromEachOther() throws Exception {
        TransactionTemplate required = template.required();
        required.execute(() -> {
            recursiveTemplate = template.required();
            return null;
        });
        assertNotSame(required, recursiveTemplate);
    }
    
    @Test
    public void testSubsequentCallsReturnDifferentTransactionScopes() throws Exception {
        assertNotSame(template.required(), template.required());
    }

}
