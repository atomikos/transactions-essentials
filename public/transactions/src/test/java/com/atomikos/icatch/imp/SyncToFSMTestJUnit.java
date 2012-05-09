package com.atomikos.icatch.imp;


import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.atomikos.finitestates.FSMEnterEvent;
import com.atomikos.icatch.Synchronization;
import com.atomikos.icatch.TxState;

public class SyncToFSMTestJUnit {

	private Synchronization mock = null;
	private SynchToFSM sync = null;
	
	@Before
	public void setUp() throws Exception {
		mock = Mockito.mock(Synchronization.class);
		sync = new SynchToFSM(mock);
	}
	
	@Test
	public void testSynchronizationNotifiedOfTerminationForReadOnly() {
		// read-only tx sees only TERMINATED, not COMMITTING or ABORTING!
		FSMEnterEvent e = new FSMEnterEvent(this,TxState.TERMINATED);
		sync.entered(e);
		Mockito.verify(mock).afterCompletion(TxState.TERMINATED);
	}
	

}
