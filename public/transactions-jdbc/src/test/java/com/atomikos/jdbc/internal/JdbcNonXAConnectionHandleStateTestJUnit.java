/**
 * Copyright (C) 2000-2020 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jdbc.internal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Stack;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.jdbc.internal.JdbcNonXAConnectionHandleState.InvalidTransactionContextException;
import com.atomikos.jdbc.internal.JdbcNonXAConnectionHandleState.ParticipantRegistrationRequiredException;
import com.atomikos.jdbc.internal.JdbcNonXAConnectionHandleState.ReadOnlyParticipantRegistrationRequiredException;
import com.atomikos.jdbc.internal.JdbcNonXAConnectionHandleState.SubTxAwareParticipantRegistrationRequiredException;
import com.atomikos.jdbc.internal.JdbcNonXAConnectionHandleState.TransactionContextException;

public class JdbcNonXAConnectionHandleStateTestJUnit {

    @Mock 
    private CompositeTransaction ct1, ct11, ct12; //nested transaction hierarchy
    
    @Mock
    private CompositeTransaction ct2; //independent transaction
    
    private JdbcNonXAConnectionHandleState state;
        
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        Mockito.when(ct1.isSameTransaction(ct1)).thenReturn(true);
        Mockito.when(ct11.isSameTransaction(ct11)).thenReturn(true);
        Mockito.when(ct12.isSameTransaction(ct12)).thenReturn(true);
        initNestedTransactionHierarchy(ct1, ct11, ct12);
        Mockito.when(ct2.isSameTransaction(ct2)).thenReturn(true);
        state = new JdbcNonXAConnectionHandleState();
    }
    
    private void initSubTransactionHierarchy(CompositeTransaction parent, CompositeTransaction subTx) {
        Mockito.when(parent.isAncestorOf(subTx)).thenReturn(true);
        Mockito.when(parent.isRelatedTransaction(subTx)).thenReturn(true);
        Mockito.when(subTx.isDescendantOf(parent)).thenReturn(true);
        Mockito.when(subTx.isRelatedTransaction(parent)).thenReturn(true);
        Stack<CompositeTransaction> lineage = new Stack<CompositeTransaction>();
        lineage.push(parent);
        Mockito.when(subTx.getLineage()).thenReturn(lineage);
    }

    private void initNestedTransactionHierarchy(CompositeTransaction parent, CompositeTransaction subTx1, CompositeTransaction subTx2) {
        initSubTransactionHierarchy(parent, subTx1);
        initSubTransactionHierarchy(parent, subTx2);
        Mockito.when(subTx1.isRelatedTransaction(subTx2)).thenReturn(true);
        Mockito.when(subTx2.isRelatedTransaction(subTx1)).thenReturn(true);
    }

    @Test(expected=ParticipantRegistrationRequiredException.class)
    public void testNotifyWithNoExistingTransactionContextThrows() throws TransactionContextException {
        state.notifyBeforeUse(ct1);
    }
    
    @Test
    public void testEnlistedReturnsTrueAfterNotify() throws TransactionContextException {
        try {
            state.notifyBeforeUse(ct1);
        } catch (ParticipantRegistrationRequiredException ok) {
        }
        assertTrue(state.isEnlistedInGlobalTransaction());
        assertTrue(state.isEnlistedInGlobalTransaction(ct1));
    }
    
    @Test
    public void testSecondNotifyWithSameTransactionContextDoesNotThrow() throws TransactionContextException {
        try {
            state.notifyBeforeUse(ct1);
        } catch (ParticipantRegistrationRequiredException ok) {
        }
        state.notifyBeforeUse(ct1);
    }
    
    @Test(expected=ReadOnlyParticipantRegistrationRequiredException.class)
    public void testNotifyReadOnlyWithNoExistingTransactionContextThrows() throws TransactionContextException {
        state = new JdbcNonXAConnectionHandleState(true);
        state.notifyBeforeUse(ct1);
    }
    
    @Test(expected=SubTxAwareParticipantRegistrationRequiredException.class)
    public void testNotifyWithSubTransactionContextThrows() throws TransactionContextException {
        try {
            state.notifyBeforeUse(ct1);
        } catch (ParticipantRegistrationRequiredException ok) {
        }
        state.notifyBeforeUse(ct12);
    }
    
    @Test(expected=InvalidTransactionContextException.class)
    public void testNotifyWithDifferentTransactionContextThrows() throws TransactionContextException {
        try {
            state.notifyBeforeUse(ct11);
        } catch (ParticipantRegistrationRequiredException ok) {
        }
        state.notifyBeforeUse(ct12);
    }
    
    @Test
    public void testTransactionTerminatedClearsTransactionContext() throws TransactionContextException {
        try {
            state.notifyBeforeUse(ct1);
        } catch (ParticipantRegistrationRequiredException ok) {
        }
        state.subTransactionTerminated();
        assertFalse(state.isEnlistedInGlobalTransaction());
        assertFalse(state.isEnlistedInGlobalTransaction(ct1));
    }
    
 
    
    @Test
    public void testSubTransactionTerminatedRestoresParentTransactionContext() throws TransactionContextException {
        try {
            state.notifyBeforeUse(ct1);
        } catch (ParticipantRegistrationRequiredException ok) {
        }
        try {
            state.notifyBeforeUse(ct12);
        } catch (SubTxAwareParticipantRegistrationRequiredException ok) {
        }
        state.subTransactionTerminated();
        assertTrue(state.isEnlistedInGlobalTransaction(ct1));
    }
    
    @Test
    public void testSubTransactionsDoingSQLFirst() throws TransactionContextException {
        try {
            state.notifyBeforeUse(ct11);
        } catch (ParticipantRegistrationRequiredException ok) {
        }
        assertTrue(state.isEnlistedInGlobalTransaction(ct11));
        state.subTransactionTerminated();
        assertTrue(state.isEnlistedInGlobalTransaction(ct1));
        try {
            state.notifyBeforeUse(ct12);
        } catch (SubTxAwareParticipantRegistrationRequiredException ok) {
        }
        assertTrue(state.isEnlistedInGlobalTransaction(ct12));
        state.subTransactionTerminated();
        assertTrue(state.isEnlistedInGlobalTransaction(ct1));
    }
  
}
