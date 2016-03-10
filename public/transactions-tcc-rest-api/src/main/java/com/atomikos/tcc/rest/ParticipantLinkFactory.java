/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.tcc.rest;


 /**
  * Customized factory to make it easy to generate ParticipantLink instances.
  * 
  */

public class ParticipantLinkFactory {


	public static ParticipantLink createInstance(String uri, String expires) {
		ParticipantLink pl = new ParticipantLink(uri,expires);
		return pl;
	}

}
