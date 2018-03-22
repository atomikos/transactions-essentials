/**
 * Copyright (C) 2000-2017 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.recovery.imp;

class DeserialisationException extends Exception {

	private static final long serialVersionUID = -3835526236269555460L;

	public DeserialisationException(String content) {
		super(content);
	}
}
