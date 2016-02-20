/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.tcc.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

@Path("/coordinator")
public interface Coordinator {

    @PUT
    @Consumes("application/tcc+json")
    @Path("/cancel")
    void cancel(Transaction transaction);

    @PUT
    @Consumes("application/tcc+json")
    @Path("/confirm")
    void confirm(Transaction transaction);

}