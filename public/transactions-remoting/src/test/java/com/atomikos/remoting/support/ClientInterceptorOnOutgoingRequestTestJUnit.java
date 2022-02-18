/**
 * Copyright (C) 2000-2022 Atomikos <info@atomikos.com>
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

public class ClientInterceptorOnOutgoingRequestTestJUnit {

	
	@Mock CompositeTransactionManager mockedCTM;
	@Mock CompositeTransaction mockedCT;
	@Mock CompositeTransaction mockedRootCT;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Configuration.getConfigProperties().setProperty(ConfigProperties.TM_UNIQUE_NAME_PROPERTY_NAME, "tmUniqueName");
		Configuration.installCompositeTransactionManager(mockedCTM);
	}

	
	ClientInterceptorTemplate sut = new ClientInterceptorTemplate();
	
	String givenCoordinatorId = "1234";
	
	String givenParentTid = "4567";
	long anyTimout = 1000l;
	
	@Test
	public void testTransactionYieldsPropagation() throws Exception {
		givenExistingTransaction(true);
		String  propagation = sut.onOutgoingRequest();
		assertNotNull(propagation);
	}

	
	@Test
	public void testNoTransactionYieldsNullPropagation() throws Exception {
		String propagation = sut.onOutgoingRequest();
		assertNull(propagation);
	}

	private CompositeTransaction givenExistingTransaction(boolean jta) {
		
		Mockito.when(mockedCTM.getCompositeTransaction()).thenReturn(mockedCT);
		Mockito.when(mockedCTM.recreateCompositeTransaction(Mockito.any())).thenReturn(mockedCT);
		Mockito.when(mockedCT.getExtent()).thenReturn(new Extent());
		Mockito.when(mockedRootCT.getTid()).thenReturn(givenParentTid);
	    Mockito.when(mockedRootCT.getProperties()).thenReturn(new Properties());

		
		Stack<CompositeTransaction> lineage = new Stack<>();
		
		lineage.push(mockedRootCT);
		
		Mockito.when(mockedCT.getLineage()).thenReturn(lineage);
		
		
		if (jta) {
			Mockito.when(mockedCT.getProperty(TransactionManagerImp.JTA_PROPERTY_NAME)).thenReturn("true");	
		}
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
