package com.atomikos.icatch;

import java.io.IOException;

import jdepend.framework.JDepend;
import org.junit.Assert;

import org.junit.Test;

public class JDependTestJunit {

	@Test
	public void test() throws IOException {
		JDepend jdepend = new JDepend();
		jdepend.addDirectory("target/classes");
		jdepend.analyze();
		Assert.assertFalse(jdepend.containsCycles());
	}

}
