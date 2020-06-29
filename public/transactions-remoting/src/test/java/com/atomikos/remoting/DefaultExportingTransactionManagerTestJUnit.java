/**
 * Copyright (C) 2000-2020 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.remoting;

import java.util.Properties;
import java.util.Stack;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.atomikos.icatch.CompositeCoordinator;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.Extent;
import com.atomikos.icatch.Synchronization;
import com.atomikos.icatch.config.Configuration;
import com.atomikos.icatch.jta.TransactionManagerImp;
import com.atomikos.icatch.provider.ConfigProperties;

public class DefaultExportingTransactionManagerTestJUnit {

	private CheckedExportingTransactionManager sut = new CheckedExportingTransactionManager(new DefaultExportingTransactionManager());

	@Mock
	CompositeTransactionManager mockedCTM;
	@Mock
	CompositeTransaction mockedCT;
	@Mock
	CompositeTransaction mockedRootCT;

	private String givenCoordinatorId = "1234";

	private String givenParentTid = "4567";
	private long anyTimout = 1000l;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Configuration.getConfigProperties().setProperty(ConfigProperties.TM_UNIQUE_NAME_PROPERTY_NAME, "tmUniqueName");
		Configuration.installCompositeTransactionManager(mockedCTM);
	}

	@Test
	public void testJtaTransactionYieldsPendingRequestSynchronization() throws Exception {
		givenExistingTransaction();
		sut.getPropagation();
		Mockito.verify(mockedCT).registerSynchronization(Mockito.any(Synchronization.class));
	}

	private CompositeTransaction givenExistingTransaction() {

		Mockito.when(mockedCTM.getCompositeTransaction()).thenReturn(mockedCT);
		Mockito.when(mockedCTM.recreateCompositeTransaction(Mockito.any())).thenReturn(mockedCT);
		Mockito.when(mockedCT.getExtent()).thenReturn(new Extent());
		Mockito.when(mockedRootCT.getTid()).thenReturn(givenParentTid);
	    Mockito.when(mockedRootCT.getProperties()).thenReturn(new Properties());
		Stack<CompositeTransaction> lineage = new Stack<>();
		lineage.push(mockedRootCT);
		Mockito.when(mockedCT.getLineage()).thenReturn(lineage);
		Mockito.when(mockedCT.getProperty(TransactionManagerImp.JTA_PROPERTY_NAME)).thenReturn("true");
		Mockito.when(mockedCT.getTimeout()).thenReturn(anyTimout);
		CompositeCoordinator compositeCoordinator = Mockito.mock(CompositeCoordinator.class);
		Mockito.when(mockedCT.getCompositeCoordinator()).thenReturn(compositeCoordinator);
		Mockito.when(mockedCT.isSerial()).thenReturn(true);
		Mockito.when(mockedCT.getTid()).thenReturn(givenCoordinatorId);
		Mockito.when(mockedCT.getProperties()).thenReturn(new Properties());
		return mockedCT;
	}
}
