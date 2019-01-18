/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.recovery.tcc.rest;

import com.atomikos.icatch.HeurRollbackException;

public interface TccTransport {

	void put(String uri) throws HeurRollbackException;

}
