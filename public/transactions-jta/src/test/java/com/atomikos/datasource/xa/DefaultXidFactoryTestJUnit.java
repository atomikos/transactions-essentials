/**
 * Copyright (C) 2000-2020 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.datasource.xa;

public class DefaultXidFactoryTestJUnit extends AbstractXidFactoryTestCase {

	protected XidFactory createXidFactory() {
		return new DefaultXidFactory();
	}

	
	
}
