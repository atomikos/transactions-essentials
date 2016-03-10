/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.tcc.rest;

public class ParticipantLink {

	protected String uri;
	protected String expires;

	public ParticipantLink() {
	}

	public void setExpires(String expires) {
		this.expires = expires;
	}
	
	public void setUri(String uri) {
		this.uri = uri;
	}
	public ParticipantLink(String uri, String expires) {
		super();
		this.uri = uri;
		this.expires = expires;
	}

	public String getUri() {
		return uri;
	}

	public String getExpires() {
		return expires;
	}

}
