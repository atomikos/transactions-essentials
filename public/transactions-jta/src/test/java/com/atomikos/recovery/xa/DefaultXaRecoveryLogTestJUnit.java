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
import com.atomikos.icatch.TxState;
import com.atomikos.recovery.CoordinatorLogEntry;
import com.atomikos.recovery.ParticipantLogEntry;
import com.atomikos.recovery.RecoveryException;
import com.atomikos.recovery.RecoveryLog;
import com.atomikos.recovery.xa.DefaultXaRecoveryLog;

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
	public void testPresumedAborting() {
		Xid xid = givenSomeXid();
		whenPresumedAborting(xid);
		thenPresumedAbortingInUnderlyingGenericLog(xid);
	}
	
	@Test
	public void testHeuristicRollbackByResource() {
		Xid xid = givenSomeXid();
		whenHeuristicRollback(xid);
		thenHeuristicRollbackInUnderlyingGenericLog(xid);
	}
	
	@Test
	public void testHeuristicCommitByResource() {
		Xid xid = givenSomeXid();
		whenHeuristicCommit(xid);
		thenHeuristicCommitInUnderlyingGenericLog(xid);
	}
	
	@Test
	public void testHeuristicHazardByResource() {
		Xid xid = givenSomeXid();
		whenHeuristicHazard(xid);
		thenHeuristicHazardInUnderlyingGenericLog(xid);
	}
	
	@Test
	public void testHeuristicMixedByResource() {
		Xid xid = givenSomeXid();
		whenHeuristicMixed(xid);
		thenHeuristicMixedInUnderlyingGenericLog(xid);
	}

	@Test
	public void testGetCommittingXids() throws RecoveryException {
		ParticipantLogEntry entry = givenCommittingParticipantLogEntryInGenericLog();
		Xid xid = whenGetExpiredCommittingXids();
		thenGtidEqualsCoordinatorId(entry, xid);
		thenParticipantEqualsXidBranchQualifierToString(entry, xid);
	}
	
	@Test
	public void testTccParticipantLogEntryIsIgnored() throws RecoveryException {
		givenCommittingTccParticipantLogEntryInGenericLog();
		Xid xid = whenGetExpiredCommittingXids();
		assertNull(xid);
	}
	
	private void givenCommittingTccParticipantLogEntryInGenericLog() throws RecoveryException {
		ParticipantLogEntry entry = new ParticipantLogEntry("tid", "http://uri", 0, "desc", TxState.COMMITTING);
		Collection<ParticipantLogEntry> c = new HashSet<ParticipantLogEntry>();
		c.add(entry);
		Mockito.when(mock.getCommittingParticipants()).thenReturn(c);		
	}

	private void thenParticipantEqualsXidBranchQualifierToString(ParticipantLogEntry entry,
			Xid xid) {
		Assert.assertEquals(entry.participantUri, XID.getBranchQualifierAsString(xid));
	}

	private void thenGtidEqualsCoordinatorId(ParticipantLogEntry entry, Xid xid) {
		Assert.assertEquals(entry.coordinatorId, XID.getGlobalTransactionIdAsString(xid));
	}

	private Xid whenGetExpiredCommittingXids() throws RecoveryException {
		Xid ret = null;
		Set<Xid> xids = sut.getExpiredCommittingXids();
		if (!xids.isEmpty()) {
			ret = xids.iterator().next();
		}
		return ret;
	}

	private ParticipantLogEntry givenCommittingParticipantLogEntryInGenericLog() throws RecoveryException {
		Xid xid = givenSomeXid();
		ParticipantLogEntry entry = new ParticipantLogEntry(XID.getGlobalTransactionIdAsString(xid), xid.toString(), 0, "desc", TxState.COMMITTING);
		Collection<ParticipantLogEntry> c = new HashSet<ParticipantLogEntry>();
		c.add(entry);
		Mockito.when(mock.getCommittingParticipants()).thenReturn(c);
		return entry;
	}

	private void thenHeuristicMixedInUnderlyingGenericLog(Xid xid) {
		ParticipantLogEntry entry = convertXidToParticipantLogEntry(xid);
		Mockito.verify(mock,Mockito.times(1)).terminatedWithHeuristicMixed(entry);
	}

	private void whenHeuristicMixed(Xid xid) {
		sut.terminatedWithHeuristicMixedByResource(xid);
	}

	private void thenHeuristicHazardInUnderlyingGenericLog(Xid xid) {
		ParticipantLogEntry entry = convertXidToParticipantLogEntry(xid);
		Mockito.verify(mock,Mockito.times(1)).terminatedWithHeuristicHazard(entry);
	}

	private void whenHeuristicHazard(Xid xid) {
		sut.terminatedWithHeuristicHazardByResource(xid);
	}

	private void thenHeuristicCommitInUnderlyingGenericLog(Xid xid) {
		ParticipantLogEntry entry = convertXidToParticipantLogEntry(xid);
		Mockito.verify(mock,Mockito.times(1)).terminatedWithHeuristicCommit(entry);
	}

	private void whenHeuristicCommit(Xid xid) {
		sut.terminatedWithHeuristicCommitByResource(xid);
	}

	private void thenHeuristicRollbackInUnderlyingGenericLog(Xid xid) {
		ParticipantLogEntry entry = convertXidToParticipantLogEntry(xid);
		Mockito.verify(mock,Mockito.times(1)).terminatedWithHeuristicRollback(entry);
	}

	private void whenHeuristicRollback(Xid xid) {
		sut.terminatedWithHeuristicRollbackByResource(xid);
	}

	private void thenPresumedAbortingInUnderlyingGenericLog(Xid xid) {
		ArgumentCaptor<ParticipantLogEntry> captor = ArgumentCaptor.forClass(ParticipantLogEntry.class);
		Mockito.verify(mock,Mockito.times(1)).presumedAborting(captor.capture());
		Assert.assertEquals(TxState.IN_DOUBT, captor.getValue().state);
	}

	private ParticipantLogEntry convertXidToParticipantLogEntry(Xid xid) {
		return new ParticipantLogEntry(XID.getGlobalTransactionIdAsString(xid), XID.getBranchQualifierAsString(xid), 0, "desc", TxState.COMMITTING);
	}

	private void whenPresumedAborting(Xid xid) {
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
