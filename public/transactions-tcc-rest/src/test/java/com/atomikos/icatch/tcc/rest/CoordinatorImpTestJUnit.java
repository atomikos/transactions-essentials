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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import junit.framework.Assert;

import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.impl.MetadataMap;
import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.atomikos.tcc.rest.Coordinator;
import com.atomikos.tcc.rest.ParticipantLink;
import com.atomikos.tcc.rest.Transaction;

public class CoordinatorImpTestJUnit {

	private static final String COORDINATOR_BASE_URL = "http://localhost:9001";
	private static final String COORDINATOR_CLIENT_URL = COORDINATOR_BASE_URL;
	private static final String PARTICIPANT_SERVER_URL_1 = "http://localhost:9002";
	private static final String PARTICIPANT_CLIENT_URL_1 = PARTICIPANT_SERVER_URL_1
			+ "/participant";
	private static final String PARTICIPANT_SERVER_URL_2 = "http://localhost:9003";
	private static final String PARTICIPANT_CLIENT_URL_2 = PARTICIPANT_SERVER_URL_2
			+ "/participant";

	private static Coordinator jsonClient;
	private static TestParticipant tp1, tp2;
	private static Server server;

	@BeforeClass
	public static void beforeClass() throws Exception {
		server = new Server(COORDINATOR_BASE_URL);
		server.start();
		jsonClient = getCoordinatorForJson();
		tp1 = new TestParticipant();
		tp1.export(PARTICIPANT_SERVER_URL_1);
		tp2 = new TestParticipant();
		tp2.export(PARTICIPANT_SERVER_URL_2);
	}

	private static void stop() {
		server.stop(true);
		tp1.stop();
		tp2.stop();
	}
	

	@AfterClass
	public static void afterClass() {
		stop();
	}

	private static Coordinator getCoordinatorForJson() {
		List<Object> providers = new ArrayList<Object>();
		providers.add(new JacksonJaxbJsonProvider());
		providers.add(new TransactionProvider());
		Coordinator c = JAXRSClientFactory.create(COORDINATOR_CLIENT_URL,
				Coordinator.class, providers);
		WebClient.client(c).accept("application/tcc+json");
		return c;
	}

	public static void main(String[] args) throws DatatypeConfigurationException, IOException {
		Transaction transaction = createTransaction(1, "http://1","http://2");
		JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
		
		
		//mapper.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, true);
		
		//mapper.configure(SerializationConfig.Feature.WRITE_ENUMS_USING_INDEX,true);
		
		provider.writeTo(transaction, 
				Transaction.class, Transaction.class,
				null, MediaType.APPLICATION_JSON_TYPE, 
				new MetadataMap<String, Object>(), 
				System.out);
		
		//provider.readFrom(type, genericType, annotations, mediaType, httpHeaders, entityStream)
		
	}

	private static Transaction createTransaction(long expiryDate,
			String... participantUris) throws DatatypeConfigurationException {
		List<ParticipantLink> participants = new ArrayList<ParticipantLink>();
		for (String uri : participantUris) {
			GregorianCalendar gcal = new GregorianCalendar();
			gcal.setTime(new Date(expiryDate));
			XMLGregorianCalendar date = DatatypeFactory.newInstance()
					.newXMLGregorianCalendar(gcal);
			ParticipantLink participantLink = new ParticipantLink();
			participantLink.setUri(uri);
			participantLink.setExpires(date);
			participants.add(participantLink);
		}
		Transaction transaction = new Transaction();
		transaction.getParticipantLinks().addAll(participants);
		return transaction;
	}

	@Before
	public void setUp() {
		tp1.reset();
		tp2.reset();
	}

	@Test
	public void testJsonCancelWorksForNonExistentParticipant()
			throws DatatypeConfigurationException, InterruptedException {

		Transaction transaction = createTransaction(System.currentTimeMillis(),
				"http://www.example.com");
		jsonClient.cancel(transaction);
	}

	@Test
	public void testJsonCancelWorksForEmptyTransaction() {
		Transaction transaction = new Transaction();
		jsonClient.cancel(transaction);
	}

	@Test
	public void testJsonConfirmWorksForEmptyTransaction() {
		Transaction transaction = new Transaction();
		jsonClient.confirm(transaction);
	}

	@Test
	public void testJsonCancelCallsDeleteOnEachParticipant()
			throws DatatypeConfigurationException {
		Transaction transaction = createTransaction(
				createSufficientExpiryDateToAvoidIntermediateTimeout(), PARTICIPANT_CLIENT_URL_1,
				PARTICIPANT_CLIENT_URL_2);
		jsonClient.cancel(transaction);
		assertTrue(tp1.wasCancelled());
		assertTrue(tp2.wasCancelled());
	}

	@Test
	public void testJsonConfirmCallsOptionsOnEachParticipant()
			throws DatatypeConfigurationException {
		Transaction transaction = createTransaction(
				createSufficientExpiryDateToAvoidIntermediateTimeout(), PARTICIPANT_CLIENT_URL_1,
				PARTICIPANT_CLIENT_URL_2);
		jsonClient.confirm(transaction);
		assertTrue(tp1.wasOptionsCalled());
		assertTrue(tp2.wasOptionsCalled());
	}

	@Test
	public void testFailureOnOptionsDoesNotPreventConfirm()
			throws DatatypeConfigurationException {
		tp1.setFailOnOptions();
		Transaction transaction = createTransaction(
				createSufficientExpiryDateToAvoidIntermediateTimeout(), PARTICIPANT_CLIENT_URL_1,
				PARTICIPANT_CLIENT_URL_2);
		jsonClient.confirm(transaction);
		assertTrue(tp1.wasConfirmed());
	}

	@Test
	public void testJsonConfirmCallsPutOnEachParticipant()
			throws DatatypeConfigurationException {
		Transaction transaction = createTransaction(
				createSufficientExpiryDateToAvoidIntermediateTimeout(), PARTICIPANT_CLIENT_URL_1,
				PARTICIPANT_CLIENT_URL_2);
		jsonClient.confirm(transaction);
		assertTrue(tp1.wasConfirmed());
		assertTrue(tp2.wasConfirmed());
	}

	@Test
	public void testJsonConfirmAfterGlobalTimeoutThrows404()
			throws DatatypeConfigurationException {
		tp1.setSimulateTimeout();
		tp2.setSimulateTimeout();
		Transaction transaction = createTransaction(
				createSufficientExpiryDateToAvoidIntermediateTimeout(), PARTICIPANT_CLIENT_URL_1,
				PARTICIPANT_CLIENT_URL_2);
		try {
			jsonClient.confirm(transaction);
			fail("No exception?");
		} catch (WebApplicationException e) {
			assertEquals(404, e.getResponse().getStatus());
		}
	}

	@Test
	public void testJsonConfirmAfterPartialTimeoutThrows409()
			throws DatatypeConfigurationException {
		tp1.setSimulateTimeout();
		Transaction transaction = createTransaction(
				createSufficientExpiryDateToAvoidIntermediateTimeout(), PARTICIPANT_CLIENT_URL_1,
				PARTICIPANT_CLIENT_URL_2);
		try {
			jsonClient.confirm(transaction);
			fail("No exception?");
		} catch (WebApplicationException e) {
			assertEquals(409, e.getResponse().getStatus());
		}
	}

	private long createSufficientExpiryDateToAvoidIntermediateTimeout() {
		return System.currentTimeMillis() + 5000;
	}

	

	@Test
	public void testConfirmIsIdempotent() throws DatatypeConfigurationException {
		Transaction transaction = createTransaction(
				createSufficientExpiryDateToAvoidIntermediateTimeout(), PARTICIPANT_CLIENT_URL_1,
				PARTICIPANT_CLIENT_URL_2);
		jsonClient.confirm(transaction);
		jsonClient.confirm(transaction);
	}

	@Test
	public void testCancelIsIdempotent() throws DatatypeConfigurationException {
		Transaction transaction = createTransaction(
				createSufficientExpiryDateToAvoidIntermediateTimeout(), PARTICIPANT_CLIENT_URL_1,
				PARTICIPANT_CLIENT_URL_2);
		jsonClient.cancel(transaction);
		jsonClient.cancel(transaction);
	}

	@Test
	public void testTimeoutMeansCancel() throws DatatypeConfigurationException {
		Transaction transaction = createTransaction(
				System.currentTimeMillis(), PARTICIPANT_CLIENT_URL_1,
				PARTICIPANT_CLIENT_URL_2);
		try {
			jsonClient.confirm(transaction);
		} catch (WebApplicationException e) {
			assertEquals(404, e.getResponse().getStatus());
			assertEquals("transaction has timed out and was cancelled", e.getMessage());
		}
	}
	
	
	@Test
	public void testParticipantLinkUriMissingThrows() throws Exception {
		Transaction transaction = createTransaction(1000L, "DUMMY");
		transaction.getParticipantLinks().get(0).setUri(null);
		try {
			jsonClient.confirm(transaction);
			fail();
		} catch (WebApplicationException e) {
			Assert.assertEquals(400, e.getResponse().getStatus());
			Assert.assertEquals("each participantLink must have a value for 'uri'", e.getMessage());
		}
	}


	@Test
	public void testParticipantExpiresMissingThrows() throws Exception {
		Transaction transaction = createTransaction(1000L, "DUMMY");
		transaction.getParticipantLinks().get(0).setExpires(null);
		try {
			jsonClient.confirm(transaction);
			fail();
		} catch (WebApplicationException e) {
			Assert.assertEquals(400, e.getResponse().getStatus());
			Assert.assertEquals("each participantLink must have an 'expires'", e.getMessage());
		}
	}
	
	@Test
	public void testJsonCancelIgnoresParticipantFailures() throws Exception {
		tp1.setSimulateErrorOnCancel();
		Transaction transaction = createTransaction(
				createSufficientExpiryDateToAvoidIntermediateTimeout(), PARTICIPANT_CLIENT_URL_1,
				PARTICIPANT_CLIENT_URL_2);
		jsonClient.cancel(transaction);
	}
}
