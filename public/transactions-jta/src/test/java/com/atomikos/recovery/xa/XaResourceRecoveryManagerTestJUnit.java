/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.recovery.xa;

import java.util.HashSet;
import java.util.Set;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.atomikos.datasource.xa.XID;
import com.atomikos.recovery.LogException;

public class XaResourceRecoveryManagerTestJUnit {

	private static final String XARESOURCE_QUALIFIER = "XARESOURCE_QUALIFIER";

	// SUT
	XaResourceRecoveryManager xaResourceRecoveryManager;

	private XAResource xaResource;

	private XaRecoveryLog log;

	@Before
	public void initMocks() {
		xaResource = Mockito.mock(XAResource.class);
		log = Mockito.mock(XaRecoveryLog.class);
		XaResourceRecoveryManager.installXaResourceRecoveryManager(log, XARESOURCE_QUALIFIER);
		xaResourceRecoveryManager = XaResourceRecoveryManager.getInstance();
	}

	@Test
	public void irrelevantResourceXidsAreIgnored() throws XAException {
		XID xid = createIrrelevantXid();
		givenXaResourceWithPreparedXid(xid);
		whenRecovered();
		thenNotTerminatedInXaResource(xid);
		thenNotTerminatedInLog(xid);
	}

	private XID createIrrelevantXid() {
		XID xid = new XID("demo", "Irrelevant" + XARESOURCE_QUALIFIER);
		return xid;
	}

	@Test
	public void relevantResourceXidsNotFoundInLogAreRolledback() throws XAException {
		Xid xid = createRelevantXid();
		givenXaResourceWithPreparedXid(xid);
		whenRecovered();
		thenRolledBackInXaResource(xid);
	}

	@Test
	public void preparingRelevantXidsAreRolledbackAfterExpiry() throws XAException, InterruptedException {
		XID xid = createRelevantXid();
		givenXaResourceWithPreparedXid(xid);
		givenExpiredPreparingXidInLog(xid);
		whenRecovered();
		thenRolledBackInXaResource(xid);
		thenTerminatedInLog(xid);
	}

	@Test
	public void preparingRelevantXidsAreNotRolledbackBeforeExpiryToMinimizeInterferenceWithOltpCommit()
			throws Exception {
		XID xid = createRelevantXid();
		givenXaResourceWithPreparedXid(xid);
		givenUnexpiredPreparingXidInLog(xid);
		whenRecovered();
		thenNotRolledbackInXaResource(xid);
		thenNotTerminatedInLog(xid);
	}

	@Test
	public void preparingRelevantXidsAreNotRolledbackAfterConcurrentOltpCommit() throws Exception {
		XID xid = createRelevantXid();
		givenXaResourceWithPreparedXid(xid);
		givenExpiredPreparingXidInLog(xid);
		givenIntermediateCommitByOltp(xid);
		whenRecovered();
		thenNotRolledbackInXaResource(xid);
		thenNotTerminatedInLog(xid);
	}

	@Test
	public void committingRelevantXidsAreForgottenAfterHeuristicRollbackByResource() throws Exception {
		XID xid = createRelevantXid();
		givenXaResourceWithHeuristicallyRolledbackXid(xid);
		givenExpiredCommittingXidInLog(xid);
		whenRecovered();
		thenForgottenInXaResource(xid);
		thenHeuristicRollbackReportedToLog(xid);
	}

	@Test
	public void rollingbackRelevantXidsAreForgottenAfterHeuristicCommitByResource() throws Exception {
		XID xid = createRelevantXid();
		givenXaResourceWithHeuristicallyCommittedXid(xid);
		givenExpiredPreparingXidInLog(xid);
		whenRecovered();
		thenForgottenInXaResource(xid);
		thenHeuristicCommitReportedToLog(xid);
	}

	@Test
	public void committingRelevantXidsAreForgottenAfterHeuristicHazardFromResource() throws Exception {
		XID xid = createRelevantXid();
		givenXaResourceWithHeuristicHazardXid(xid);
		givenExpiredCommittingXidInLog(xid);
		whenRecovered();
		thenForgottenInXaResource(xid);
		thenHeuristicHazardReportedToLog(xid);
	}

	@Test
	public void rollingbackRelevantXidsAreForgottenAfterHeuristicHazardFromResource() throws Exception {
		XID xid = createRelevantXid();
		givenXaResourceWithHeuristicHazardXid(xid);
		givenExpiredPreparingXidInLog(xid);
		whenRecovered();
		thenForgottenInXaResource(xid);
		thenHeuristicHazardReportedToLog(xid);
	}

	@Test
	public void rollingbackRelevantXidsAreForgottenAfterHeuristicMixedFromResource() throws Exception {
		XID xid = createRelevantXid();
		givenXaResourceWithHeuristicMixedXid(xid);
		givenExpiredPreparingXidInLog(xid);
		whenRecovered();
		thenForgottenInXaResource(xid);
		thenHeuristicMixedReportedToLog(xid);
	}

	@Test
	public void concurrentIntermediateRollbackInResourceDoesNotThrow() throws XAException {
		Xid xid = createRelevantXid();
		givenXaResourceWithPreparedXid(xid);
		givenExpiredPreparingXidInLog(xid);
		givenIntermediateRollbackByOltp(xid);
		whenRecovered();
	}

	@Test
	public void autoForgetHeuristicsCanBeDisabledByConfiguration() throws Exception {
		XID xid = createRelevantXid();
		givenAutoForgetDisabled();
		givenXaResourceWithHeuristicMixedXid(xid);
		givenExpiredPreparingXidInLog(xid);
		whenRecovered();
		thenNotForgottenInXaResource(xid);
		thenHeuristicMixedReportedToLog(xid);
	}

	private void givenAutoForgetDisabled() {
		xaResourceRecoveryManager.setAutoForgetHeuristicsOnRecovery(false);
	}

	private void givenIntermediateRollbackByOltp(Xid xid) throws XAException {
		Mockito.doThrow(new XAException(XAException.XAER_NOTA)).when(xaResource).rollback(xid);
	}

	private void givenXaResourceWithHeuristicHazardXid(Xid xid) throws XAException {
		Xid[] xids = new Xid[1];
		xids[0] = xid;
		Mockito.when(xaResource.recover(Mockito.anyInt())).thenReturn(xids);
		Mockito.doThrow(new XAException(XAException.XA_HEURHAZ)).when(xaResource).rollback(xid);
		Mockito.doThrow(new XAException(XAException.XA_HEURHAZ)).when(xaResource).commit(xid, false);
	}

	private void givenXaResourceWithHeuristicallyCommittedXid(Xid xid) throws XAException {
		Xid[] xids = new Xid[1];
		xids[0] = xid;
		Mockito.when(xaResource.recover(Mockito.anyInt())).thenReturn(xids);
		Mockito.doThrow(new XAException(XAException.XA_HEURCOM)).when(xaResource).rollback(xid);
	}

	private void givenXaResourceWithHeuristicMixedXid(Xid xid) throws XAException {
		Xid[] xids = new Xid[1];
		xids[0] = xid;
		Mockito.when(xaResource.recover(Mockito.anyInt())).thenReturn(xids);
		Mockito.doThrow(new XAException(XAException.XA_HEURMIX)).when(xaResource).rollback(xid);
		Mockito.doThrow(new XAException(XAException.XA_HEURMIX)).when(xaResource).commit(xid, false);
	}

	private void givenXaResourceWithHeuristicallyRolledbackXid(Xid xid) throws XAException {
		Xid[] xids = new Xid[1];
		xids[0] = xid;
		Mockito.when(xaResource.recover(Mockito.anyInt())).thenReturn(xids);
		Mockito.doThrow(new XAException(XAException.XA_HEURRB)).when(xaResource).commit(xid, false);
	}

	private void thenForgottenInXaResource(Xid xid) throws XAException {
		Mockito.verify(xaResource, Mockito.times(1)).forget(xid);
	}

	private void thenNotForgottenInXaResource(Xid xid) throws XAException {
		Mockito.verify(xaResource, Mockito.times(0)).forget(xid);
	}

	private void thenNotTerminatedInLog(XID xid) {
		Mockito.verify(log, Mockito.times(0)).terminated(xid);
	}

	@Test
	public void committingRelevantXidsAreCommittedAfterExpiry() throws Exception {
		XID xid = createRelevantXid();
		givenXaResourceWithPreparedXid(xid);
		givenExpiredCommittingXidInLog(xid);
		whenRecovered();
		thenCommittedInXaResource(xid);
		thenTerminatedInLog(xid);
	}

	@Test
	public void failuresOnXaResourceRecoverDoNotThrow() throws Exception {
		givenXaResourceWithExceptionOnRecover();
		whenRecovered();
	}

	private void givenXaResourceWithExceptionOnRecover() throws XAException {
		Mockito.when(xaResource.recover(Mockito.anyInt())).thenThrow(new XAException(XAException.XAER_RMERR));
	}

	private void thenTerminatedInLog(XID xid) {
		Mockito.verify(log, Mockito.times(1)).terminated(xid);
	}

	private void thenHeuristicHazardReportedToLog(XID xid) throws LogException {
		Mockito.verify(log, Mockito.times(1)).terminatedWithHeuristicHazardByResource(xid);
	}

	private void thenHeuristicMixedReportedToLog(XID xid) throws LogException {
		Mockito.verify(log, Mockito.times(1)).terminatedWithHeuristicMixedByResource(xid);
	}

	private void thenHeuristicCommitReportedToLog(XID xid) throws LogException {
		Mockito.verify(log, Mockito.times(1)).terminatedWithHeuristicCommitByResource(xid);
	}

	private void thenHeuristicRollbackReportedToLog(XID xid) throws LogException {
		Mockito.verify(log, Mockito.times(1)).terminatedWithHeuristicRollbackByResource(xid);
	}

	private void thenCommittedInXaResource(Xid xid) throws XAException {
		Mockito.verify(xaResource, Mockito.times(1)).commit(xid, false);
	}

	private void givenUnexpiredPreparingXidInLog(XID xid) throws IllegalStateException, LogException {
		Mockito.doThrow(new IllegalStateException()).when(log).presumedAborting(xid);
	}

	private void givenExpiredPreparingXidInLog(Xid xid) {
		// do nothing: don't make log mock throw on presumedAborting
	}

	private void givenExpiredCommittingXidInLog(XID xid) throws LogException {
		Set<XID> toReturn = new HashSet<XID>();
		toReturn.add(xid);
		Mockito.when(log.getExpiredCommittingXids()).thenReturn(toReturn);
	}

	private void givenIntermediateCommitByOltp(XID xid) throws IllegalStateException, LogException {
		Mockito.doThrow(new IllegalStateException()).when(log).presumedAborting(xid);
	}

	private void thenRolledBackInXaResource(Xid xid) throws XAException {
		Mockito.verify(xaResource, Mockito.times(1)).rollback(xid);
	}

	private void thenNotRolledbackInXaResource(Xid xid) throws XAException {
		Mockito.verify(xaResource, Mockito.never()).rollback(xid);
	}

	private XID createRelevantXid() {
		XID xid = new XID("demo", XARESOURCE_QUALIFIER);
		return xid;
	}

	private void thenNotTerminatedInXaResource(Xid xid) throws XAException {
		Mockito.verify(xaResource, Mockito.never()).commit(xid, false);
		Mockito.verify(xaResource, Mockito.never()).rollback(xid);
	}

	private void whenRecovered() {
		xaResourceRecoveryManager.recover(xaResource);
	}

	private void givenXaResourceWithPreparedXid(Xid xid) throws XAException {
		Xid[] xids = new Xid[1];
		xids[0] = xid;
		Mockito.when(xaResource.recover(Mockito.anyInt())).thenReturn(xids);
	}

}
