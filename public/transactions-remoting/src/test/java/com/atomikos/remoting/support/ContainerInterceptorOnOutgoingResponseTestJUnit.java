/**
 * Copyright (C) 2000-2020 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.remoting.support;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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
import com.atomikos.icatch.config.Configuration;
import com.atomikos.icatch.jta.TransactionManagerImp;
import com.atomikos.icatch.provider.ConfigProperties;
import com.atomikos.remoting.twopc.AtomikosRestPort;

public class ContainerInterceptorOnOutgoingResponseTestJUnit {
	ContainerInterceptorTemplate sut = new ContainerInterceptorTemplate();


	@Mock CompositeTransactionManager mockedCTM;
	@Mock CompositeTransaction mockedCT;
	@Mock CompositeTransaction mockedRootCT;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Configuration.getConfigProperties().setProperty(ConfigProperties.TM_UNIQUE_NAME_PROPERTY_NAME, "tmUniqueName");
		Configuration.installCompositeTransactionManager(mockedCTM);
		AtomikosRestPort.setUrl("http://bla");
	}
	String transactionId = "1234";
	long anyTimout = 1000l;
	String givenParentTid = "4567";
	String givenCoordinatorId = "1234";

	@Test
	public void testNoTransactionReturnsNull() throws Exception {
		String extent = sut.onOutgoingResponse(false);
		assertNull(extent);
	}

	@Test
	public void testTransactionWithoutErrorCommits() throws Exception {
		givenExistingTransaction();
		sut.onOutgoingResponse(false);
		Mockito.verify(mockedCT).commit();
	}

	@Test
	public void testTransactionWithErrorAborts() throws Exception {
		givenExistingTransaction();
		sut.onOutgoingResponse(true);
		Mockito.verify(mockedCT).rollback();
	}


	@Test
	public void testTransactionWithoutErrorReturnsExtent() throws Exception {
		givenExistingTransaction();
		String extent = sut.onOutgoingResponse(false);
		assertNotNull(extent);
	}

	@Test
	public void testTransactionWithErrorsReturnsNull() throws Exception {
		 givenExistingTransaction();
		String extent = sut.onOutgoingResponse(true);
		assertNull(extent);
	}

	private CompositeTransaction givenExistingTransaction() {

		Mockito.when(mockedCTM.getCompositeTransaction()).thenReturn(mockedCT);
		Mockito.when(mockedCTM.recreateCompositeTransaction(Mockito.any())).thenReturn(mockedCT);
		Mockito.when(mockedCT.getExtent()).thenReturn(new Extent());
		Mockito.when(mockedRootCT.getTid()).thenReturn(givenParentTid);

		Stack<CompositeTransaction> lineage = new Stack<>();

		lineage.push(mockedRootCT);

		Mockito.when(mockedCT.getLineage()).thenReturn(lineage);

		Mockito.when(mockedCT.getProperty(TransactionManagerImp.JTA_PROPERTY_NAME)).thenReturn("true");
		Mockito.when(mockedCT.getTimeout()).thenReturn(anyTimout);
		CompositeCoordinator compositeCoordinator = Mockito.mock(CompositeCoordinator.class);
		//
		Mockito.when(mockedCT.getCompositeCoordinator()).thenReturn(compositeCoordinator);
		Mockito.when(mockedCT.isSerial()).thenReturn(true);
		Mockito.when(mockedCT.getTid()).thenReturn(givenCoordinatorId);
		Mockito.when(mockedCT.getProperties()).thenReturn(new Properties());
		return mockedCT;
	}
}
