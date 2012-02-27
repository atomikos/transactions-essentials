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
