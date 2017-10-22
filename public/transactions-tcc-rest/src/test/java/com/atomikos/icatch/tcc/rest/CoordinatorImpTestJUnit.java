/**
 * Copyright (C) 2000-2017 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.tcc.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.Response;
import javax.xml.datatype.DatatypeConfigurationException;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.atomikos.tcc.rest.ParticipantLink;
import com.atomikos.tcc.rest.Transaction;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

public class CoordinatorImpTestJUnit {

	private static final String COORDINATOR_BASE_URL = "http://localhost:9001";
	private static final String COORDINATOR_CLIENT_URL = COORDINATOR_BASE_URL;
	private static final String PARTICIPANT_SERVER_URL_1 = "http://localhost:9002";
	private static final String PARTICIPANT_CLIENT_URL_1 = PARTICIPANT_SERVER_URL_1
			+ "/participant";
	private static final String PARTICIPANT_SERVER_URL_2 = "http://localhost:9003";
	private static final String PARTICIPANT_CLIENT_URL_2 = PARTICIPANT_SERVER_URL_2
			+ "/participant";

	private static WebTarget coordinator;
	private static TestParticipant tp1, tp2;
	private static Server server;

	@BeforeClass
	public static void beforeClass() throws Exception {
		server = new Server(COORDINATOR_BASE_URL);
		server.start();
		coordinator = getCoordinatorForJson();
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

	private static WebTarget getCoordinatorForJson() {
		Client client = ClientBuilder.newClient();
		client.register(new JacksonJaxbJsonProvider());
		client.register(new TransactionProvider());
		WebTarget target = client.target(COORDINATOR_CLIENT_URL);
		return target.path("/coordinator");
	}

	public static void main(String[] args) throws DatatypeConfigurationException, IOException {
		Transaction transaction = createTransaction("2002-05-30T09:30:10Z", "http://1","http://2");
		JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
		
		provider.writeTo(transaction, 
				Transaction.class, Transaction.class,
				null, MediaType.APPLICATION_JSON_TYPE, 
				new MultivaluedHashMap<String, Object>(), 
				System.out);
		
	}

	private static Transaction createTransaction(String date,
			String... participantUris) throws DatatypeConfigurationException {
		List<ParticipantLink> participants = new ArrayList<ParticipantLink>();
		for (String uri : participantUris) {
			ParticipantLink participantLink = new ParticipantLink(uri,date);
			participants.add(participantLink);
		}
		Transaction transaction = new Transaction();
		transaction.getParticipantLinks().addAll(participants);
		return transaction;
	}

	private static Transaction createTransaction(long timestamp,
			String... participantUris) throws DatatypeConfigurationException {
		List<ParticipantLink> participants = new ArrayList<ParticipantLink>();
		for (String uri : participantUris) {
			ParticipantLink participantLink = new ParticipantLink(uri,timestamp);
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
		coordinator.path("/cancel").request().put(Entity.entity(transaction, "application/tcc+json"));
	}

	@Test
	public void testJsonCancelWorksForEmptyTransaction() {
		Transaction transaction = new Transaction();
		coordinator.path("/cancel").request().put(Entity.entity(transaction, "application/tcc+json"));
	}

	@Test
	public void testJsonConfirmWorksForEmptyTransaction() {
		Transaction transaction = new Transaction();
		coordinator.path("/confirm").request().put(Entity.entity(transaction, "application/tcc+json"));
	}

	@Test
	public void testJsonCancelCallsDeleteOnEachParticipant()
			throws DatatypeConfigurationException {
		Transaction transaction = createTransaction(
				createSufficientExpiryDateToAvoidIntermediateTimeout(), PARTICIPANT_CLIENT_URL_1,
				PARTICIPANT_CLIENT_URL_2);
		coordinator.path("/cancel").request().put(Entity.entity(transaction, "application/tcc+json"));
		assertTrue(tp1.wasCancelled());
		assertTrue(tp2.wasCancelled());
	}

	@Test
	public void testJsonConfirmCallsOptionsOnEachParticipant()
			throws DatatypeConfigurationException {
		Transaction transaction = createTransaction(
				createSufficientExpiryDateToAvoidIntermediateTimeout(), PARTICIPANT_CLIENT_URL_1,
				PARTICIPANT_CLIENT_URL_2);
		coordinator.path("/confirm").request().put(Entity.entity(transaction, "application/tcc+json"));
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
		coordinator.path("/confirm").request().put(Entity.entity(transaction, "application/tcc+json"));
		assertTrue(tp1.wasConfirmed());
	}

	@Test
	public void testJsonConfirmCallsPutOnEachParticipant()
			throws DatatypeConfigurationException {
		Transaction transaction = createTransaction(
				createSufficientExpiryDateToAvoidIntermediateTimeout(), PARTICIPANT_CLIENT_URL_1,
				PARTICIPANT_CLIENT_URL_2);
		coordinator.path("/confirm").request().put(Entity.entity(transaction, "application/tcc+json"));
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
			Response r = coordinator.path("/confirm").request().put(Entity.entity(transaction, "application/tcc+json"));
			assertEquals(404, r.getStatus());
		
	}

	@Test
	public void testJsonConfirmAfterPartialTimeoutThrows409()
			throws DatatypeConfigurationException {
		tp1.setSimulateTimeout();
		Transaction transaction = createTransaction(
				createSufficientExpiryDateToAvoidIntermediateTimeout(), PARTICIPANT_CLIENT_URL_1,
				PARTICIPANT_CLIENT_URL_2);
			Response r = coordinator.path("/confirm").request().put(Entity.entity(transaction, "application/tcc+json"));
			assertEquals(409, r.getStatus());
	}

	private long createSufficientExpiryDateToAvoidIntermediateTimeout() {
		return System.currentTimeMillis() + 5000;
	}

	

	@Test
	public void testConfirmIsIdempotent() throws DatatypeConfigurationException {
		Transaction transaction = createTransaction(
				createSufficientExpiryDateToAvoidIntermediateTimeout(), PARTICIPANT_CLIENT_URL_1,
				PARTICIPANT_CLIENT_URL_2);
		coordinator.path("/confirm").request().put(Entity.entity(transaction, "application/tcc+json"));
		coordinator.path("/confirm").request().put(Entity.entity(transaction, "application/tcc+json"));
	}

	@Test
	public void testCancelIsIdempotent() throws DatatypeConfigurationException {
		Transaction transaction = createTransaction(
				createSufficientExpiryDateToAvoidIntermediateTimeout(), PARTICIPANT_CLIENT_URL_1,
				PARTICIPANT_CLIENT_URL_2);
		coordinator.path("/cancel").request().put(Entity.entity(transaction, "application/tcc+json"));
		coordinator.path("/cancel").request().put(Entity.entity(transaction, "application/tcc+json"));
	}

	@Test
	public void testTimeoutMeansCancel() throws DatatypeConfigurationException {
		Transaction transaction = createTransaction(
				System.currentTimeMillis(), PARTICIPANT_CLIENT_URL_1,
				PARTICIPANT_CLIENT_URL_2);
		
			Response r =coordinator.path("/confirm").request().put(Entity.entity(transaction, "application/tcc+json"));
			assertEquals(404, r.getStatus());
			assertEquals("transaction has timed out and was cancelled", getMessage(r));
		
	}
	
	
	@Test
	public void testParticipantLinkUriMissingThrows() throws Exception {
		Transaction transaction = createTransaction(1000L,new String []{ null});
			Response r =coordinator.path("/confirm").request().put(Entity.entity(transaction, "application/tcc+json"));
			assertEquals(400, r.getStatus());
			assertEquals("each participantLink must have a value for 'uri'", getMessage(r));
	}

	@Test
	public void testParticipantMalformedExpiresThrows() throws Exception {
		String invalidISO8601Date = "participantLink: expires must be a valid ISO 8601 date";
		Transaction transaction = createTransaction(invalidISO8601Date, "DUMMY");
			Response r =coordinator.path("/confirm").request().put(Entity.entity(transaction, "application/tcc+json"));
			assertEquals(400, r.getStatus());
			assertEquals("invalid date format for participantLink 'expires': "+invalidISO8601Date, getMessage(r));
	}
	
	
	@Test
	public void testParticipantExpiresMissingThrows() throws Exception {
		Transaction transaction = createTransaction(null, "DUMMY");
		Response r =coordinator.path("/confirm").request().put(Entity.entity(transaction, "application/tcc+json"));
		assertEquals(400, r.getStatus());
		assertEquals("each participantLink must have an 'expires'", getMessage(r));
	}

	private String getMessage(Response r) {
		InputStream in = (InputStream) r.getEntity();
		String message = new Scanner(in,"UTF-8").useDelimiter("\\A").next();
        return message;
	}
	
	@Test
	public void testJsonCancelIgnoresParticipantFailures() throws Exception {
		tp1.setSimulateErrorOnCancel();
		Transaction transaction = createTransaction(
				createSufficientExpiryDateToAvoidIntermediateTimeout(), PARTICIPANT_CLIENT_URL_1,
				PARTICIPANT_CLIENT_URL_2);
		coordinator.path("/cancel").request().put(Entity.entity(transaction, "application/tcc+json"));
	}
}
