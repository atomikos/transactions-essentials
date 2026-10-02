/**
 * Copyright (C) 2000-2026 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.imp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.SubTxAwareParticipant;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.config.Configuration;

/**
 * Specifies the kernel invariants that the LRCO late-enrollment adapter
 * pattern relies on:
 *
 *   (1) SubTxAwareParticipant.committed() callbacks are invoked in
 *       registration order on the tx where they were registered.
 *
 *   (2) A Participant added via tx.addParticipant() from inside a
 *       SubTxAwareParticipant.committed() callback ends up AFTER any
 *       previously-enrolled Participants in the coordinator's participant
 *       list.
 *
 *   (3) When multiple SubTxAwareParticipants each add a Participant in
 *       their committed() callback, the resulting Participant order
 *       matches the SubTxAware registration order.
 *
 * Together these invariants make the "register SubTxAware on first
 * Kafka/Rabbit/Qpid-1.0/... API call, add LRCO-Participant from the
 * committed() callback" pattern viable without kernel changes.
 *
 * If any of these tests fail in the future, either the kernel semantics
 * changed (and the late-enrollment pattern needs revision) or the
 * pattern was misimplemented.
 */
public class LateEnrollmentInvariantsTestJUnit {

    private CompositeTransactionManager ctm;

    @Before
    public void setUp() throws Exception {
        Configuration.installCompositeTransactionManager(new CompositeTransactionManagerImp());
        Configuration.init();
        ctm = Configuration.getCompositeTransactionManager();
    }

    @After
    public void tearDown() throws Exception {
        Configuration.shutdown(true);
    }

    @Test
    public void subTxAwareCommittedCalledInRegistrationOrder() throws Exception {
        CompositeTransaction ct = ctm.createCompositeTransaction(10_000);
        List<String> callOrder = new ArrayList<>();

        ct.addSubTxAwareParticipant(new RecordingSubTxAware("A", callOrder));
        ct.addSubTxAwareParticipant(new RecordingSubTxAware("B", callOrder));
        ct.addSubTxAwareParticipant(new RecordingSubTxAware("C", callOrder));

        ct.commit();

        Assert.assertEquals("SubTxAware callbacks must run in registration order",
                java.util.Arrays.asList("A", "B", "C"), callOrder);
    }

    /**
     * LOAD-BEARING test: this is the core invariant that the LRCO
     * late-enrollment adapter pattern depends on. If this fails, the
     * adapter pattern breaks and we cannot guarantee that the
     * Kafka/Rabbit/Qpid-1.0/... Participant lands in the LRCO position.
     */
    @Test
    public void participantAddedFromSubTxAwareCommittedIsLastInParticipantOrder() throws Exception {
        CompositeTransaction ct = ctm.createCompositeTransaction(10_000);

        // Eager: enroll a Participant directly, like a JDBC XA-resource would
        RecordingParticipant earlyParticipant = new RecordingParticipant("early");
        ct.addParticipant(earlyParticipant);

        // Late: register a SubTxAware that adds a Participant from its callback
        // (this is exactly how the LRCO adapter would behave)
        RecordingParticipant lateParticipant = new RecordingParticipant("late");
        ct.addSubTxAwareParticipant(new SubTxAwareParticipant() {
            @Override
            public void committed(CompositeTransaction transaction) {
                transaction.addParticipant(lateParticipant);
            }
            @Override
            public void rolledback(CompositeTransaction transaction) {}
        });

        ct.commit();

        List<Participant> participants = collectParticipants(ct);
        int earlyIdx = participants.indexOf(earlyParticipant);
        int lateIdx = participants.indexOf(lateParticipant);

        Assert.assertTrue("Early participant must be present", earlyIdx >= 0);
        Assert.assertTrue("Late participant added from committed() must be present", lateIdx >= 0);
        Assert.assertTrue("Late participant must come AFTER early participant in 2PC order",
                lateIdx > earlyIdx);
    }

    @Test
    public void multipleSubTxAwaresAddingParticipantsRetainRegistrationOrder() throws Exception {
        CompositeTransaction ct = ctm.createCompositeTransaction(10_000);

        RecordingParticipant pA = new RecordingParticipant("A");
        RecordingParticipant pB = new RecordingParticipant("B");
        RecordingParticipant pC = new RecordingParticipant("C");

        ct.addSubTxAwareParticipant(new ParticipantAddingSubTxAware(pA));
        ct.addSubTxAwareParticipant(new ParticipantAddingSubTxAware(pB));
        ct.addSubTxAwareParticipant(new ParticipantAddingSubTxAware(pC));

        ct.commit();

        List<Participant> participants = collectParticipants(ct);
        int idxA = participants.indexOf(pA);
        int idxB = participants.indexOf(pB);
        int idxC = participants.indexOf(pC);

        Assert.assertTrue("A must be present", idxA >= 0);
        Assert.assertTrue("B must come after A", idxB > idxA);
        Assert.assertTrue("C must come after B", idxC > idxB);
    }

    // --- helpers ---

    private List<Participant> collectParticipants(CompositeTransaction ct) {
        CoordinatorImp coord = (CoordinatorImp) ct.getCompositeCoordinator();
        return new ArrayList<>(coord.getParticipants());
    }

    private static class RecordingSubTxAware implements SubTxAwareParticipant {
        private final String name;
        private final List<String> sink;
        RecordingSubTxAware(String name, List<String> sink) {
            this.name = name;
            this.sink = sink;
        }
        @Override
        public void committed(CompositeTransaction transaction) {
            sink.add(name);
        }
        @Override
        public void rolledback(CompositeTransaction transaction) {}
    }

    private static class ParticipantAddingSubTxAware implements SubTxAwareParticipant {
        private final Participant toAdd;
        ParticipantAddingSubTxAware(Participant toAdd) {
            this.toAdd = toAdd;
        }
        @Override
        public void committed(CompositeTransaction transaction) {
            transaction.addParticipant(toAdd);
        }
        @Override
        public void rolledback(CompositeTransaction transaction) {}
    }

    private static final AtomicInteger UNIQUE = new AtomicInteger(0);

    private static class RecordingParticipant implements Participant {
        private final String label;
        private final int unique = UNIQUE.incrementAndGet();
        RecordingParticipant(String label) {
            this.label = label;
        }
        @Override
        public String getURI() {
            return "test://" + label + "/" + unique;
        }
        @Override
        public void setCascadeList(Map<String, Integer> allParticipants) throws SysException {}
        @Override
        public void setGlobalSiblingCount(int count) {}
        @Override
        public int prepare() {
            return 0x01;
        }
        @Override
        public void commit(boolean onePhase) {}
        @Override
        public void rollback() {}
        @Override
        public void forget() {}
        @Override
        public String getResourceName() {
            return null;
        }
        @Override
        public boolean equals(Object other) {
            if (!(other instanceof RecordingParticipant)) return false;
            return unique == ((RecordingParticipant) other).unique;
        }
        @Override
        public int hashCode() {
            return unique;
        }
        @Override
        public String toString() {
            return "RecordingParticipant[" + label + "#" + unique + "]";
        }
    }
}
