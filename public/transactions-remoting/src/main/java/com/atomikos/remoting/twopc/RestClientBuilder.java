package com.atomikos.remoting.twopc;

import javax.ws.rs.client.Client;

/**
 * Abstract Builder for creation of Rest Client
 * 
 * You can create a sublcass if you need some special handling like connection polling, timeouts, ...
 * Implementation is defined by the property com.atomikos.remoting.rest_client_builder
 * Default in transaction-default.properties is
 * com.atomikos.remoting.rest_client_builder=com.atomikos.remoting.twopc.DefaultRestClientBuilder
 * 
 * Here some example:
 * <pre>
 * 
 * public class PooledRestClientBuilder extends RestClientBuilder {

	@Override
	public Client build() {
		ResteasyClientBuilder builder = new ResteasyClientBuilder();
		
		ConfigProperties configProperties = Configuration.getConfigProperties();
		String connectionPoolSizeProperty = configProperties.getProperty("com.atomikos.remoting.twopc.ParticipantAdapter.connectionPoolSize");
		int connectionPoolSize = 20;
		if (connectionPoolSizeProperty != null)
			connectionPoolSize = Integer.valueOf(connectionPoolSizeProperty);
		
		String connectTimeoutProperty = configProperties.getProperty("com.atomikos.remoting.twopc.ParticipantAdapter.connectTimeout");
		int connectTimeout = 10;
		if (connectTimeoutProperty != null)
			connectTimeout = Integer.valueOf(connectTimeoutProperty);

		String readTimeoutProperty = configProperties.getProperty("com.atomikos.remoting.twopc.ParticipantAdapter.readTimeout");
		int readTimeout = 60;
		if (readTimeoutProperty != null)
			readTimeout = Integer.valueOf(readTimeoutProperty);

		builder.connectTimeout(connectTimeout, TimeUnit.SECONDS);
		builder.readTimeout(readTimeout, TimeUnit.SECONDS);
		Client c = builder.connectionPoolSize(connectionPoolSize).build(); 
		c.property("jersey.config.client.suppressHttpComplianceValidation", true);
		c.register(ParticipantsProvider.class);
		return c;
	}
}
 * 
 * </pre>
 */
public abstract class RestClientBuilder {
	
	public abstract Client build();

}
