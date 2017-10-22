/**
 * Copyright (C) 2000-2017 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.tcc.rest;

import java.io.Serializable;

import com.atomikos.icatch.HeurRollbackException;

interface ParticipantAdapter extends Serializable {
	String getUri();
	void delete();
	void put() throws HeurRollbackException;
	void options();
	long getExpires();
}
