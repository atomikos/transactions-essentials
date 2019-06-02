/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.remoting.taas;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.RollbackException;
import com.atomikos.remoting.support.HeaderNames;

@Path("atomikos")
@Produces(HeaderNames.MimeType.APPLICATION_VND_ATOMIKOS_JSON)
@Consumes(HeaderNames.MimeType.APPLICATION_VND_ATOMIKOS_JSON)
public interface RestTransactionService {

	@POST
	@Path("/begin")
	String begin(@QueryParam("timeout") Long timeout);

	@POST
	@Path("/commit")
	void commit(String... extents)
			throws HeurRollbackException, HeurMixedException,
			HeurHazardException, RollbackException;

	@POST
	@Path("/rollback")
	void rollback(String... extents)
			throws HeurRollbackException, HeurMixedException,
			HeurHazardException, RollbackException;

}