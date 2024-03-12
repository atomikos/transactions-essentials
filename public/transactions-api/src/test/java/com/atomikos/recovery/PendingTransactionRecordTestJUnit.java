/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.recovery;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.junit.Test;

public class PendingTransactionRecordTestJUnit {

	PendingTransactionRecord given;
	@Test
	public void testToRecord() throws Exception {
		String id = "id1";
		TxState state = TxState.COMMITTING;
		long expires = 100l;
		given = new PendingTransactionRecord(id, state, expires, "domain");
		String record = given.toRecord();
		assertEquals("id1|COMMITTING|100|domain|"+System.lineSeparator(), record);
	}
	@Test
	public void testFromRecord() throws Exception {
		String givenRecord = "id1|COMMITTING|100|domain|123";
		PendingTransactionRecord pendingTransactionRecord = PendingTransactionRecord.fromRecord(givenRecord);
		assertEquals("id1", pendingTransactionRecord.id);
		assertEquals(TxState.COMMITTING, pendingTransactionRecord.state);
		assertEquals(100, pendingTransactionRecord.expires);
		assertEquals("123", pendingTransactionRecord.superiorId);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testFromPartialRecordThrows() {
		String givenRecord = "id1|COMMITTING|";
		PendingTransactionRecord.fromRecord(givenRecord);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testFromInvalidRecordThrows() {
		String givenRecord = "id1-COMMITTING-";
		PendingTransactionRecord.fromRecord(givenRecord);
	}
	
	@Test
	public void testRootIsNativeInItsOwnDomain() {
	    given = new PendingTransactionRecord("id", TxState.IN_DOUBT, 0, "domain");
	    assertFalse(given.isForeignInDomain("domain"));
	}

	@Test
	public void testLocalSubtransactionIsNativeInItsOwnDomain() {
	    given = new PendingTransactionRecord("id", TxState.IN_DOUBT, 0, "domain", "localSuperiorId");
        assertFalse(given.isForeignInDomain("domain"));
	}
	
	@Test
    public void testImportedTransactionIsForeignInImportingDomain() {
        given = new PendingTransactionRecord("id", TxState.IN_DOUBT, 0, "domain", "localSuperiorId");
        assertTrue(given.isForeignInDomain("importingDomain"));
    }
	
	@Test
	public void testRootIsRecoveredByItsOwnDomain() {
	    given = new PendingTransactionRecord("id", TxState.IN_DOUBT, 0, "domain");
	    assertTrue(given.isRecoveredByDomain("domain"));
	    
	}
	
	@Test
	public void testImportedTransactionWithoutSuperiorUrlIsRecoveredByImportingDomain() {
	    given = new PendingTransactionRecord("id", TxState.IN_DOUBT, 0, "foreignDomain", "superiorId");
        assertTrue(given.isRecoveredByDomain("importingDomain")); 
	}
	
	@Test
    public void testImportedTransactionWithSuperiorUrlIsNotRecoveredByImportingDomain() {
        given = new PendingTransactionRecord("id", TxState.IN_DOUBT, 0, "foreignDomain", "https://superiorId");
        assertFalse(given.isRecoveredByDomain("importingDomain")); 
    }
	
	
	@Test
	public void testLocalSubtransactionIsRecoveredByItsOwnDomain() {
	    given = new PendingTransactionRecord("id", TxState.IN_DOUBT, 0, "domain", "localSuperiorId");
        assertTrue(given.isRecoveredByDomain("domain")); 
	}
	
	@Test
	public void testRootDoesNotAllowHeuristicAbort() {
	    given = new PendingTransactionRecord("id", TxState.IN_DOUBT, 0, "domain");
	    assertFalse(given.allowsHeuristicTermination("domain"));
	}
	
	@Test
	public void testLocalSubtransactionDoesNotAllowHeuristicAbort() {
	    given = new PendingTransactionRecord("id", TxState.IN_DOUBT, 0, "domain", "localSuperiorId");
	    assertFalse(given.allowsHeuristicTermination("domain"));
	}
	
	@Test
	public void testImportedTransactionWithoutSuperiorUrlAllowsHeuristicAbortByImportingDomain() {
	    given = new PendingTransactionRecord("id", TxState.IN_DOUBT, 0, "foreignDomain", "superiorId");
	    assertTrue(given.allowsHeuristicTermination("importingDomain"));
	}
	
	@Test
	public void testImportedTransactionWithSuperiorUrlDoesNotAllowHeuristicAbortByImportingDomain() {
	    given = new PendingTransactionRecord("id", TxState.IN_DOUBT, 0, "foreignDomain", "http://superiorId");	
	    assertFalse(given.allowsHeuristicTermination("importingDomain"));
	}
	
	@Test
	public void testImportedTransactionFromSameDomainDoesNotAllowheuristicAbort() {
	    given = new PendingTransactionRecord("id", TxState.IN_DOUBT, 0, "domain", "http://superiorId/"); 
        assertFalse(given.allowsHeuristicTermination("domain"));
	}
	
	@Test
	public void testFindAllDescendants() {
	    given = new PendingTransactionRecord("rootId",  TxState.IN_DOUBT, 0, "domain");
	    Collection<PendingTransactionRecord> set = new HashSet<PendingTransactionRecord>();
	    set.add(new PendingTransactionRecord("id",  TxState.IN_DOUBT, 0, "domain", "superiorId"));
	    set.add(new PendingTransactionRecord("superiorId",  TxState.IN_DOUBT, 0, "domain", "rootId"));
	    set.add(given);
	    Collection<PendingTransactionRecord> result = PendingTransactionRecord.findAllDescendants(given, set);
	    assertEquals(2, result.size());
	}
	
	@Test
    public void testRemoveAllDescendants() {
        given = new PendingTransactionRecord("rootId",  TxState.IN_DOUBT, 0, "domain");
        Collection<PendingTransactionRecord> set = new HashSet<PendingTransactionRecord>();
        set.add(new PendingTransactionRecord("id",  TxState.IN_DOUBT, 0, "domain", "superiorId"));
        set.add(new PendingTransactionRecord("superiorId",  TxState.IN_DOUBT, 0, "domain", "rootId"));
        set.add(given);
        PendingTransactionRecord.removeAllDescendants(given, set);
        assertEquals(1, set.size());
    }
	
	@Test
	public void testExtractCoordinatorIds() {
        given = new PendingTransactionRecord("rootId",  TxState.IN_DOUBT, 0, "domain");
        Collection<String> result = PendingTransactionRecord.extractCoordinatorIds(Collections.singleton(given), TxState.IN_DOUBT);
        assertFalse(result.isEmpty());
        result = PendingTransactionRecord.extractCoordinatorIds(Collections.singleton(given), TxState.COMMITTING);
        assertTrue(result.isEmpty());
	}
}
