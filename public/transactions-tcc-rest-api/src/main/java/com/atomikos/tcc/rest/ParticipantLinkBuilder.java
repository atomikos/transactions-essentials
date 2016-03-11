/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.tcc.rest;


public class ParticipantLinkBuilder {

	public static ParticipantLinkBuilder instance(String uri, String expiryDate) {
		return new ParticipantLinkBuilder(uri, expiryDate);
	}

	private ParticipantLink pl;
	
	private ParticipantLinkBuilder(String uri, String expiryDate) {
		this.pl = new ParticipantLink(uri, expiryDate);
	}
	
	
	
	public ParticipantLink build() {
		assertNotNull(pl.getExpires(), "Required: expiry");
		return this.pl;
	}

	private void assertNotNull(Object o, String msg) {
		if (o == null) throw new IllegalStateException(msg);
	}



}
