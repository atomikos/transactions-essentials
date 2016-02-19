/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * This code ("Atomikos TransactionsEssentials"), by itself,
 * is being distributed under the
 * Apache License, Version 2.0 ("License"), a copy of which may be found at
 * http://www.atomikos.com/licenses/apache-license-2.0.txt .
 * You may not use this file except in compliance with the License.
 *
 * While the License grants certain patent license rights,
 * those patent license rights only extend to the use of
 * Atomikos TransactionsEssentials by itself.
 *
 * This code (Atomikos TransactionsEssentials) contains certain interfaces
 * in package (namespace) com.atomikos.icatch
 * (including com.atomikos.icatch.Participant) which, if implemented, may
 * infringe one or more patents held by Atomikos.
 * It should be appreciated that you may NOT implement such interfaces;
 * licensing to implement these interfaces must be obtained separately from Atomikos.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package com.atomikos.recovery.xa;

import static org.junit.Assert.assertNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.transaction.xa.Xid;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.atomikos.datasource.xa.XID;
import com.atomikos.icatch.ParticipantLogEntry;
import com.atomikos.icatch.TxState;
import com.atomikos.recovery.LogReadException;
import com.atomikos.recovery.LogException;
import com.atomikos.recovery.RecoveryLog;

public class DefaultXaRecoveryLogTestJUnit {

	private static final String TID = "TID";
	private static final String BRANCH = "BRANCH";
	private DefaultXaRecoveryLog sut;
	private RecoveryLog mock;
	
	@Before
	public void setUp() throws Exception {
		mock = Mockito.mock(RecoveryLog.class);
		sut = new DefaultXaRecoveryLog(mock);
		
	}

	@Test
	public void testTerminated() {
		XID xid = givenSomeXid();
		whenTerminated(xid);
		thenTerminatedInUnderlyingGenericLog(xid);
	}
	
	@Test
	public void testPresumedAborting() throws IllegalStateException, LogException {
		XID xid = givenSomeXid();
		whenPresumedAborting(xid);
		thenPresumedAbortingInUnderlyingGenericLog(xid);
	}
	
	@Test
	public void testHeuristicRollbackByResource() throws IllegalStateException, LogException {
		XID xid = givenSomeXid();
		whenHeuristicRollback(xid);
		thenHeuristicRollbackInUnderlyingGenericLog(xid);
	}
	
	@Test
	public void testHeuristicCommitByResource() throws IllegalStateException, LogException {
		XID xid = givenSomeXid();
		whenHeuristicCommit(xid);
		thenHeuristicCommitInUnderlyingGenericLog(xid);
	}
	
	@Test
	public void testHeuristicHazardByResource() throws IllegalStateException, LogException {
		XID xid = givenSomeXid();
		whenHeuristicHazard(xid);
		thenHeuristicHazardInUnderlyingGenericLog(xid);
	}
	
	@Test
	public void testHeuristicMixedByResource() throws IllegalStateException, LogException {
		XID xid = givenSomeXid();
		whenHeuristicMixed(xid);
		thenHeuristicMixedInUnderlyingGenericLog(xid);
	}

	@Test
	public void testGetCommittingXids() throws LogReadException {
		ParticipantLogEntry entry = givenCommittingParticipantLogEntryInGenericLog();
		XID xid = whenGetExpiredCommittingXids();
		thenGtidEqualsCoordinatorId(entry, xid);
		thenParticipantEqualsXidBranchQualifierToString(entry, xid);
	}
	
	@Test
	public void testTccParticipantLogEntryIsIgnored() throws LogReadException {
		givenCommittingTccParticipantLogEntryInGenericLog();
		Xid xid = whenGetExpiredCommittingXids();
		assertNull(xid);
	}
	
	private void givenCommittingTccParticipantLogEntryInGenericLog() throws LogReadException {
		ParticipantLogEntry entry = new ParticipantLogEntry("tid", "http://uri", 0, "desc", TxState.COMMITTING);
		Collection<ParticipantLogEntry> c = new HashSet<ParticipantLogEntry>();
		c.add(entry);
		Mockito.when(mock.getCommittingParticipants()).thenReturn(c);		
	}

	private void thenParticipantEqualsXidBranchQualifierToString(ParticipantLogEntry entry,
			XID xid) {
		Assert.assertEquals(entry.uri,xid.getBranchQualifierAsString());
	}

	private void thenGtidEqualsCoordinatorId(ParticipantLogEntry entry, XID xid) {
		Assert.assertEquals(entry.coordinatorId, xid.getGlobalTransactionIdAsString());
	}

	private XID whenGetExpiredCommittingXids() throws LogReadException {
		XID ret = null;
		Set<XID> xids = sut.getExpiredCommittingXids();
		if (!xids.isEmpty()) {
			ret = xids.iterator().next();
		}
		return ret;
	}

	private ParticipantLogEntry givenCommittingParticipantLogEntryInGenericLog() throws LogReadException {
		XID xid = givenSomeXid();
		ParticipantLogEntry entry = new ParticipantLogEntry(xid.getGlobalTransactionIdAsString(), xid.toString(), 0, "desc", TxState.COMMITTING);
		Collection<ParticipantLogEntry> c = new HashSet<ParticipantLogEntry>();
		c.add(entry);
		Mockito.when(mock.getCommittingParticipants()).thenReturn(c);
		return entry;
	}

	private void thenHeuristicMixedInUnderlyingGenericLog(XID xid) throws LogException {
		ParticipantLogEntry entry = convertXidToParticipantLogEntry(xid);
		Mockito.verify(mock,Mockito.times(1)).terminatedWithHeuristicMixed(entry);
	}

	private void whenHeuristicMixed(XID xid) throws LogException {
		sut.terminatedWithHeuristicMixedByResource(xid);
	}

	private void thenHeuristicHazardInUnderlyingGenericLog(XID xid) throws LogException {
		ParticipantLogEntry entry = convertXidToParticipantLogEntry(xid);
		Mockito.verify(mock,Mockito.times(1)).terminatedWithHeuristicHazard(entry);
	}

	private void whenHeuristicHazard(XID xid) throws LogException {
		sut.terminatedWithHeuristicHazardByResource(xid);
	}

	private void thenHeuristicCommitInUnderlyingGenericLog(XID xid) throws LogException {
		ParticipantLogEntry entry = convertXidToParticipantLogEntry(xid);
		Mockito.verify(mock,Mockito.times(1)).terminatedWithHeuristicCommit(entry);
	}

	private void whenHeuristicCommit(XID xid) throws LogException {
		sut.terminatedWithHeuristicCommitByResource(xid);
	}

	private void thenHeuristicRollbackInUnderlyingGenericLog(XID xid) throws LogException {
		ParticipantLogEntry entry = convertXidToParticipantLogEntry(xid);
		Mockito.verify(mock,Mockito.times(1)).terminatedWithHeuristicRollback(entry);
	}

	private void whenHeuristicRollback(XID xid) throws LogException {
		sut.terminatedWithHeuristicRollbackByResource(xid);
	}

	private void thenPresumedAbortingInUnderlyingGenericLog(Xid xid) throws IllegalStateException, LogException {
		ArgumentCaptor<ParticipantLogEntry> captor = ArgumentCaptor.forClass(ParticipantLogEntry.class);
		Mockito.verify(mock,Mockito.times(1)).presumedAborting(captor.capture());
		Assert.assertEquals(TxState.IN_DOUBT, captor.getValue().state);
	}

	private ParticipantLogEntry convertXidToParticipantLogEntry(XID xid) {
		return new ParticipantLogEntry(xid.getGlobalTransactionIdAsString(), xid.getBranchQualifierAsString(), 0, "desc", TxState.COMMITTING);
	}

	private void whenPresumedAborting(XID xid) throws IllegalStateException, LogException {
		sut.presumedAborting(xid);
	}

	private void thenTerminatedInUnderlyingGenericLog(XID xid) {
		ParticipantLogEntry entry = convertXidToParticipantLogEntry(xid);
		Mockito.verify(mock,Mockito.times(1)).terminated(entry);
	}

	private void whenTerminated(XID xid) {
		sut.terminated(xid);
	}

	private XID givenSomeXid() {
		return new XID(TID, BRANCH);
	}

}
