/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.remoting;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.atomikos.icatch.CompositeCoordinator;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.Extent;
import com.atomikos.icatch.Propagation;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.config.Configuration;
import com.atomikos.remoting.twopc.AtomikosRestPort;

public class DefaultImportingTransactionManagerTestJUnit {
	
	private static final String BASE_URL ="http://localhost:8088/";
	private static final String PROPAGATION =
			"version=2019,domain=DOMAIN,timeout=1234,serial=true,recoveryCoordinatorURI=PARENT,parent=ROOT,parent=PARENT,property.key=value";;

	private Propagation propagation;
	DefaultImportingTransactionManager sut;
	
	@Mock CompositeTransactionManager mockedCTM;
	@Mock CompositeTransaction mockedCT;
	@Mock CompositeCoordinator mockedCC;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Mockito.when(mockedCTM.getCompositeTransaction()).thenReturn(mockedCT);
		Mockito.when(mockedCTM.recreateCompositeTransaction(Mockito.any())).thenReturn(mockedCT);
		Mockito.when(mockedCT.getExtent()).thenReturn(new Extent());
		Mockito.when(mockedCT.getCompositeCoordinator()).thenReturn(mockedCC);
		Mockito.when(mockedCC.getRootId()).thenReturn("ROOT");
		Configuration.installCompositeTransactionManager(mockedCTM);
		sut = new DefaultImportingTransactionManager();
		propagation = new Parser().parsePropagation(PROPAGATION);
		AtomikosRestPort.setUrl(BASE_URL);
	}

	@Test
	public void testImportRecreatesCompositeTransaction() {
		sut.importTransaction(propagation);
		Mockito.verify(mockedCTM).recreateCompositeTransaction(Mockito.any());
	}
	
	@Test
	public void testTerminatedWithSuccessCompletesExtent() throws SysException, RollbackException {
		sut.importTransaction(propagation);		
		Extent completedExtent = sut.terminated(true);
		assertTrue(completedExtent.toString().indexOf(BASE_URL) >=0);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testImportThrowsForNullPropagation() {
		sut.importTransaction(null);
	}
	
	@Test
	public void testTerminatedReturnsNullForFailure() throws Exception {
		sut.importTransaction(propagation);
		
		Extent extent = sut.terminated(false);
		assertNull(extent);
		Mockito.verify(mockedCT).rollback();
	}
	
	@Test(expected=RollbackException.class)
	public void testTerminatedThrowsIfNoActiveTransaction() throws Exception {
		Mockito.when(mockedCTM.getCompositeTransaction()).thenReturn(null);
		sut.terminated(true);
	}
}
