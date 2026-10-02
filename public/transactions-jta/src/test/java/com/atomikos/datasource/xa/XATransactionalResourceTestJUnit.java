/**
 * Copyright (C) 2000-2026 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.datasource.xa;

import javax.transaction.xa.XAResource;

import junit.framework.TestCase;

/**
 * Tests the resource-name length constraint at the
 * {@link XATransactionalResource} API. The kernel constraint is
 * {@code length > 64 - MAX_LONG_LEN} (where MAX_LONG_LEN is the
 * byte-length of Long.MAX_VALUE's decimal form, i.e. 19), giving a
 * maximum acceptable length of 45 bytes (lengths 1-45 pass, lengths
 * &gt;= 46 fail). Tests fix the limit in code so that any future
 * change to the limit triggers a visible test failure rather than a
 * silent contract drift.
 */
public class XATransactionalResourceTestJUnit extends TestCase {

	private static final int MAX_NAME_LENGTH = 45;

	private static class FixtureResource extends XATransactionalResource {
		FixtureResource(String name) {
			super(name);
		}

		@Override
		protected XAResource refreshXAConnection() {
			return null;
		}
	}

	private String stringOfLength(int n) {
		StringBuilder sb = new StringBuilder(n);
		for (int i = 0; i < n; i++) {
			sb.append('a');
		}
		return sb.toString();
	}

	public void testResourceNameAtTheLimitIsAccepted() {
		String name = stringOfLength(MAX_NAME_LENGTH);
		new FixtureResource(name); // must not throw
	}

	public void testResourceNameJustOverTheLimitIsRejected() {
		String name = stringOfLength(MAX_NAME_LENGTH + 1);
		try {
			new FixtureResource(name);
			fail("Expected RuntimeException for resource name of length "
					+ (MAX_NAME_LENGTH + 1));
		} catch (RuntimeException expected) {
			assertTrue("Exception message should mention the limit; was: "
					+ expected.getMessage(),
					expected.getMessage().contains("Max length of resource name exceeded"));
		}
	}
}
