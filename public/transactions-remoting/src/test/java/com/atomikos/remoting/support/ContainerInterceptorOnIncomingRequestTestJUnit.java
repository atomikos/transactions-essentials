/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.remoting.support;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.atomikos.icatch.ImportingTransactionManager;

public class ContainerInterceptorOnIncomingRequestTestJUnit {

	private static final String PROPAGATION_WITHOUT_PROPERTIES = "version=2019,domain=DOMAIN,timeout=1234,serial=true,recoveryCoordinatorURI=PARENT,parent=ROOT,parent=PARENT";

	ContainerInterceptorTemplate sut = new ContainerInterceptorTemplate();

	ImportingTransactionManager mockedImportingTransactionManager;

	@Before
	public void setup() {
		mockedImportingTransactionManager = Mockito.mock(ImportingTransactionManager.class);
		sut.setImportingTransactionManager(mockedImportingTransactionManager);
	}

	@Test
	public void testNullPropagationIsIgnored() throws Exception {
		String propagation = null;
		sut.onIncomingRequest(propagation);
		Mockito.verify(mockedImportingTransactionManager, Mockito.never()).importTransaction(Mockito.any());
	}

	@Test
	public void testValidPropagationHeaderIsImported() throws Exception {
		sut.onIncomingRequest(PROPAGATION_WITHOUT_PROPERTIES);
		Mockito.verify(mockedImportingTransactionManager).importTransaction(Mockito.any());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInValidPropagationHeaderThrows() throws Exception {
		sut.onIncomingRequest("bla");
	}
}
