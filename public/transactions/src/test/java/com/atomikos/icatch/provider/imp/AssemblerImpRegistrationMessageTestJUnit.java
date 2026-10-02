/**
 * Copyright (C) 2000-2026 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.provider.imp;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AssemblerImpRegistrationMessageTestJUnit {

	private PrintStream originalOut;
	private ByteArrayOutputStream captured;

	@BeforeClass
	public static void skipOnCommercialEdition() {
		// The RegisterYourDownload prompt is open-source-only. On the
		// commercial edition (stable), AssemblerImp.initializeProperties()
		// calls SubscriptionVerifier.verifyWithWarnings() instead and emits
		// a different message about subscription files. Detect the
		// commercial edition by SubscriptionVerifier class presence on
		// the classpath and skip — keeps this test mergeable into stable
		// without it failing there.
		boolean commercialEdition;
		try {
			Class.forName("com.atomikos.subscription.SubscriptionVerifier");
			commercialEdition = true;
		} catch (ClassNotFoundException expected) {
			commercialEdition = false;
		}
		Assume.assumeFalse(
				"SubscriptionVerifier present — commercial edition; this test verifies open-source-only behavior.",
				commercialEdition);
	}

	@Before
	public void captureStdout() {
		originalOut = System.out;
		captured = new ByteArrayOutputStream();
		System.setOut(new PrintStream(captured));
	}

	@After
	public void restoreStdout() {
		System.setOut(originalOut);
	}

	@Test
	public void registrationMessageIsEmittedOnStdoutWhenFlagAbsent() {
		AssemblerImp sut = new AssemblerImp();
		sut.initializeProperties();
		String out = captured.toString();
		Assert.assertTrue(
				"Expected the RegisterYourDownload prompt on stdout when com.atomikos.icatch.registered is absent from classpath properties; got: " + out,
				out.contains("Register for free") && out.contains("RegisterYourDownload")
						&& out.contains("go live"));
	}
}
