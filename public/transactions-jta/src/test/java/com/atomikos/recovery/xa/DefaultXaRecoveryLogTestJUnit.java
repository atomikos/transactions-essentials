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
		Xid xid = givenSomeXid();
		whenTerminated(xid);
		thenTerminatedInUnderlyingGenericLog(xid);
	}
	
	@Test
	public void testPresumedAborting() throws IllegalStateException, LogException {
		Xid xid = givenSomeXid();
		whenPresumedAborting(xid);
		thenPresumedAbortingInUnderlyingGenericLog(xid);
	}
	
	@Test
	public void testHeuristicRollbackByResource() throws IllegalStateException, LogException {
		Xid xid = givenSomeXid();
		whenHeuristicRollback(xid);
		thenHeuristicRollbackInUnderlyingGenericLog(xid);
	}
	
	@Test
	public void testHeuristicCommitByResource() throws IllegalStateException, LogException {
		Xid xid = givenSomeXid();
		whenHeuristicCommit(xid);
		thenHeuristicCommitInUnderlyingGenericLog(xid);
	}
	
	@Test
	public void testHeuristicHazardByResource() throws IllegalStateException, LogException {
		Xid xid = givenSomeXid();
		whenHeuristicHazard(xid);
		thenHeuristicHazardInUnderlyingGenericLog(xid);
	}
	
	@Test
	public void testHeuristicMixedByResource() throws IllegalStateException, LogException {
		Xid xid = givenSomeXid();
		whenHeuristicMixed(xid);
		thenHeuristicMixedInUnderlyingGenericLog(xid);
	}

	@Test
	public void testGetCommittingXids() throws LogReadException {
		ParticipantLogEntry entry = givenCommittingParticipantLogEntryInGenericLog();
		Xid xid = whenGetExpiredCommittingXids();
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
			Xid xid) {
		Assert.assertEquals(entry.uri, XID.getBranchQualifierAsString(xid));
	}

	private void thenGtidEqualsCoordinatorId(ParticipantLogEntry entry, Xid xid) {
		Assert.assertEquals(entry.coordinatorId, XID.getGlobalTransactionIdAsString(xid));
	}

	private Xid whenGetExpiredCommittingXids() throws LogReadException {
		Xid ret = null;
		Set<Xid> xids = sut.getExpiredCommittingXids();
		if (!xids.isEmpty()) {
			ret = xids.iterator().next();
		}
		return ret;
	}

	private ParticipantLogEntry givenCommittingParticipantLogEntryInGenericLog() throws LogReadException {
		Xid xid = givenSomeXid();
		ParticipantLogEntry entry = new ParticipantLogEntry(XID.getGlobalTransactionIdAsString(xid), xid.toString(), 0, "desc", TxState.COMMITTING);
		Collection<ParticipantLogEntry> c = new HashSet<ParticipantLogEntry>();
		c.add(entry);
		Mockito.when(mock.getCommittingParticipants()).thenReturn(c);
		return entry;
	}

	private void thenHeuristicMixedInUnderlyingGenericLog(Xid xid) throws LogException {
		ParticipantLogEntry entry = convertXidToParticipantLogEntry(xid);
		Mockito.verify(mock,Mockito.times(1)).terminatedWithHeuristicMixed(entry);
	}

	private void whenHeuristicMixed(Xid xid) throws LogException {
		sut.terminatedWithHeuristicMixedByResource(xid);
	}

	private void thenHeuristicHazardInUnderlyingGenericLog(Xid xid) throws LogException {
		ParticipantLogEntry entry = convertXidToParticipantLogEntry(xid);
		Mockito.verify(mock,Mockito.times(1)).terminatedWithHeuristicHazard(entry);
	}

	private void whenHeuristicHazard(Xid xid) throws LogException {
		sut.terminatedWithHeuristicHazardByResource(xid);
	}

	private void thenHeuristicCommitInUnderlyingGenericLog(Xid xid) throws LogException {
		ParticipantLogEntry entry = convertXidToParticipantLogEntry(xid);
		Mockito.verify(mock,Mockito.times(1)).terminatedWithHeuristicCommit(entry);
	}

	private void whenHeuristicCommit(Xid xid) throws LogException {
		sut.terminatedWithHeuristicCommitByResource(xid);
	}

	private void thenHeuristicRollbackInUnderlyingGenericLog(Xid xid) throws LogException {
		ParticipantLogEntry entry = convertXidToParticipantLogEntry(xid);
		Mockito.verify(mock,Mockito.times(1)).terminatedWithHeuristicRollback(entry);
	}

	private void whenHeuristicRollback(Xid xid) throws LogException {
		sut.terminatedWithHeuristicRollbackByResource(xid);
	}

	private void thenPresumedAbortingInUnderlyingGenericLog(Xid xid) throws IllegalStateException, LogException {
		ArgumentCaptor<ParticipantLogEntry> captor = ArgumentCaptor.forClass(ParticipantLogEntry.class);
		Mockito.verify(mock,Mockito.times(1)).presumedAborting(captor.capture());
		Assert.assertEquals(TxState.IN_DOUBT, captor.getValue().state);
	}

	private ParticipantLogEntry convertXidToParticipantLogEntry(Xid xid) {
		return new ParticipantLogEntry(XID.getGlobalTransactionIdAsString(xid), XID.getBranchQualifierAsString(xid), 0, "desc", TxState.COMMITTING);
	}

	private void whenPresumedAborting(Xid xid) throws IllegalStateException, LogException {
		sut.presumedAborting(xid);
	}

	private void thenTerminatedInUnderlyingGenericLog(Xid xid) {
		ParticipantLogEntry entry = convertXidToParticipantLogEntry(xid);
		Mockito.verify(mock,Mockito.times(1)).terminated(entry);
	}

	private void whenTerminated(Xid xid) {
		sut.terminated(xid);
	}

	private Xid givenSomeXid() {
		return new XID(TID, BRANCH);
	}

}
