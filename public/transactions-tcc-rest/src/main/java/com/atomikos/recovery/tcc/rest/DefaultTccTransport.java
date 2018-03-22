/**
 * Copyright (C) 2000-2017 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.recovery.tcc.rest;

import javax.ws.rs.client.WebTarget;

import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.tcc.rest.ParticipantAdapterImp;

public class DefaultTccTransport implements TccTransport {

	@Override
	public void put(String uri) throws HeurRollbackException {
		WebTarget target = ParticipantAdapterImp.createJaxRsClientForUri(uri);
		ParticipantAdapterImp.callConfirmOnJaxrsClient(target);
	}

}
