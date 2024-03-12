/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.remoting.support;

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

public class ClientInterceptorOnIncomingResponseTestJUnit {
	ClientInterceptorTemplate sut = new ClientInterceptorTemplate();

	long anyTimout = 1000l;

	@Mock
	CompositeTransactionManager mockedCTM;
	@Mock
	CompositeTransaction mockedCT;
	@Mock
	CompositeTransaction mockedRootCT;



	private static final String EXTENT = 
			"version=2019,parent=PARENT,uri=URI_1,responseCount=1,direct=true,uri=URI_2,responseCount=2,direct=false";
	
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Configuration.getConfigProperties().setProperty(ConfigProperties.TM_UNIQUE_NAME_PROPERTY_NAME, "tmUniqueName");
		Configuration.installCompositeTransactionManager(mockedCTM);
	}

	
	@Test
	public void testTransactionWithExtentWorks() throws Exception {
		givenExistingTransaction();
		sut.onIncomingResponse(EXTENT);
	}
	
	
	@Test
	public void testNoTransactionWithNullExtentWorks() throws Exception {
		sut.onIncomingResponse(null);	
	}

	private CompositeTransaction givenExistingTransaction() {

		Mockito.when(mockedCTM.getCompositeTransaction()).thenReturn(mockedCT);
		Mockito.when(mockedCTM.recreateCompositeTransaction(Mockito.any())).thenReturn(mockedCT);
		Mockito.when(mockedCT.getExtent()).thenReturn(new Extent());
		Mockito.when(mockedRootCT.getTid()).thenReturn("ROOT");
		Stack<CompositeTransaction> lineage = new Stack<>();
		lineage.push(mockedRootCT);
		Mockito.when(mockedCT.getLineage()).thenReturn(lineage);
		Mockito.when(mockedCT.getProperty(TransactionManagerImp.JTA_PROPERTY_NAME)).thenReturn("true");
		Mockito.when(mockedCT.getTimeout()).thenReturn(anyTimout);
		CompositeCoordinator compositeCoordinator = Mockito.mock(CompositeCoordinator.class);
		Mockito.when(mockedCT.getCompositeCoordinator()).thenReturn(compositeCoordinator);
		Mockito.when(mockedCT.isSerial()).thenReturn(true);
		Mockito.when(mockedCT.getTid()).thenReturn("PARENT");
		Mockito.when(mockedCT.getProperties()).thenReturn(new Properties());
		return mockedCT;
	}
}
