/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.tcc.rest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class ParticipantLink {

	private String uri;
	private String expires;

	/**
	 * required by third-party REST frameworks 
	 */
	@SuppressWarnings("unused")
	private ParticipantLink() {}

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
	public ParticipantLink(String uri, long expires) {
		this(uri, toDate(expires));
	}
	
	private static String toDate(long timestamp) {
		Date date = new Date(timestamp);
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		format.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
		return format.format(date.getTime());
	}

	public String getUri() {
		return uri;
	}

	public String getExpires() {
		return expires;
	}

}
