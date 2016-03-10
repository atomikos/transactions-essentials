/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.tcc.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.xml.datatype.DatatypeConfigurationException;

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
		Transaction transaction = createTransaction("2002-05-30T09:30:10Z", "http://1","http://2");
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
		Transaction transaction = createTransaction(1000L,new String []{ null});
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
		Transaction transaction = createTransaction(null, "DUMMY");
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
