/**
 * Copyright (C) 2000-2022 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.util;

import static com.atomikos.util.Atomikos.VERSION;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ProductVersionTestJUnit  {

	@Test
	public void checkAtomikosVersion() {
		assertEquals("X.Y.Z", VERSION);
	}

}
