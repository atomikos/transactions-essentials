/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * This code ("Atomikos TransactionsEssentials"), by itself,
 * is being distributed under the
 * Apache License, Version 2.0 ("License"), a copy of which may be found at
 * http://www.atomikos.com/licenses/apache-license-2.0.txt .
 * You may not use this file except in compliance with the License.
 *
 * While the License grants certain patent license rights,
 * those patent license rights only extend to the use of
 * Atomikos TransactionsEssentials by itself.
 *
 * This code (Atomikos TransactionsEssentials) contains certain interfaces
 * in package (namespace) com.atomikos.icatch
 * (including com.atomikos.icatch.Participant) which, if implemented, may
 * infringe one or more patents held by Atomikos.
 * It should be appreciated that you may NOT implement such interfaces;
 * licensing to implement these interfaces must be obtained separately from Atomikos.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package com.atomikos.icatch.tcc.rest;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.apache.cxf.jaxrs.provider.JSONProvider;

import com.atomikos.tcc.rest.ParticipantLink;
@Path("/participant")
public class TestParticipant  {
	
	private JAXRSServerFactoryBean sf;
	private boolean cancelCalled;
	private boolean confirmCalled;
	private boolean simulateTimeout;
	private boolean optionsCalled;
	private boolean failOnOptions;
	private boolean failOnCancel;
	
	public TestParticipant() {
	}
 	
	public void stop() {
		if (sf != null) sf.getBus().shutdown(true);
		sf = null;
	}

	public void export(String url) {
		sf = new JAXRSServerFactoryBean();
        JSONProvider prov = new JSONProvider();
        List<String> mimeTypes = new ArrayList<String>();
        mimeTypes.add(MimeTypes.MIME_TYPE_PARTICIPANT_V1);
        prov.setProduceMediaTypes(mimeTypes);
        Map<String,String> nsMap = new HashMap<String,String>();
        nsMap.put("http://www.atomikos.com/tcc/rest/v1", "tcc");
        prov.setNamespaceMap(nsMap);
        sf.setProvider(prov);
        sf.setResourceClasses(TestParticipant.class);
        sf.setResourceProvider(TestParticipant.class,
            new SingletonResourceProvider(this));
        sf.setAddress(url);
        sf.create();
	}

	
	@DELETE
	@Consumes("application/tcc")
	public void cancel() {
		cancelCalled = true;
		if (failOnCancel) throw new WebApplicationException();
	}

	
	ParticipantLink getConfirmLink() throws DatatypeConfigurationException {
		GregorianCalendar gcal = new GregorianCalendar();
		gcal.setTime(new Date());
		XMLGregorianCalendar date = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
		ParticipantLink confirm = new ParticipantLink();
		confirm.setUri("http://www.example.com/participant");
		confirm.setExpires(date);
		return confirm;
	}
	
	@PUT
	@Consumes("application/tcc")
	public void confirm(@Context HttpHeaders headers) {
		assertAcceptHeaderPresent(headers);
		confirmCalled = true;
		if (simulateTimeout) throw new WebApplicationException(404);
	}
	
	private void assertAcceptHeaderPresent(HttpHeaders headers) {
		if ( ! headers.getRequestHeader("Accept").contains(MimeTypes.MIME_TYPE_PARTICIPANT_V1)) throw new WebApplicationException(406);
		if ( ! headers.getRequestHeader("Content-Type").contains(MimeTypes.MIME_TYPE_PARTICIPANT_V1)) throw new WebApplicationException(406);
	}

	@PUT
	@Consumes("text/plain") 
	public void confirmWithWrongAccept() {
		throw new WebApplicationException(404);
	}
	
	public void setSimulateTimeout() {
		simulateTimeout = true;
	}
	
	public boolean wasCancelled() {
		return cancelCalled;
	}
	
	public void reset() {
		cancelCalled = false;
		confirmCalled = false;
		simulateTimeout = false;
		optionsCalled = false;
		failOnOptions = false;
		failOnCancel = false;
	}

	public boolean wasConfirmed() {
		return confirmCalled;
	}

	@OPTIONS
	public void options() {
		optionsCalled = true;
		if (failOnOptions) throw new WebApplicationException(500);
	}

	public boolean wasOptionsCalled() {
		return optionsCalled;
	}

	public void setFailOnOptions() {
		failOnOptions = true;
	}

	public void setSimulateErrorOnCancel() {
		failOnCancel = true;
	}

	
	public static void main(String[] args) throws Exception {
		new TestParticipant().export("http://localhost:9000/");
		System.out.println("Server ready...");
		Thread.sleep(5 * 60 * 1000);
		System.out.println("Server exiting");
		System.exit(0);
	}
}
