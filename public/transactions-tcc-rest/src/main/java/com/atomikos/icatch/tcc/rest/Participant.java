/**
 * Copyright (C) 2000-2017 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.tcc.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;

interface Participant {

    @DELETE
    @Consumes("application/tcc")
    void cancel();

    @PUT
    @Consumes("application/tcc")
    void confirm();

}