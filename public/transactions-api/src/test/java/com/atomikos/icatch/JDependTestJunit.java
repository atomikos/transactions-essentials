/**
 * Copyright (C) 2000-2023 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import jdepend.framework.JDepend;

public class JDependTestJunit {

	@Test
	public void test() throws IOException {
		JDepend jdepend = new JDepend();
		jdepend.addDirectory("target/classes");
		jdepend.analyze();
		Assert.assertFalse(jdepend.containsCycles());
	}

}
