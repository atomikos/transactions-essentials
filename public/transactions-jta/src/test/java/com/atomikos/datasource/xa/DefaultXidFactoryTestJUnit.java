package com.atomikos.datasource.xa;

public class DefaultXidFactoryTestJUnit extends AbstractXidFactoryTestCase {

	protected XidFactory createXidFactory() {
		return new DefaultXidFactory();
	}

	
	
}
