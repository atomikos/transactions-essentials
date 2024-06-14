package com.atomikos.remoting.twopc;

import static javax.ws.rs.client.ClientBuilder.newClient;

import javax.ws.rs.client.Client;

/**
 * Default provider for standard Jaxrs Client without connection pool
 */
public class DefaultRestClientBuilder extends RestClientBuilder {

	public Client build() {
		Client client = newClient();
		client.property("jersey.config.client.suppressHttpComplianceValidation", true);
		client.register(ParticipantsProvider.class);
		return client;	
	}
}
