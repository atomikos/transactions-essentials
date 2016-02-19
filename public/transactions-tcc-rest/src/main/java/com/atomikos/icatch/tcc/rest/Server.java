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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;

import com.atomikos.icatch.config.Configuration;

/**
 * Simple server application to run a TCC/REST coordinator service.
 * 
 * By default, plain HTTP is used. To enable HTTPS: 
 * 
 * <ol>
 *  <li>Construct your own app and create an instance of this class</li>
 * 	<li>Pass an https://... URL as constructor startup parameter</li>
 *  <li>Initialize the CXF bus for HTTPS by setting the default bus (see CXF docs)</li>
 * 	<li>Call start on the server object</li>
 * </ol>
 *
 */

public class Server {

	private String url;
	private JAXRSServerFactoryBean sf;
	
	public Server(String url) {
		this.url = url;
	}
	
	public void start() {
		startTransactionCore();
		startCoordinatorService();
	}

	private void startCoordinatorService() {
		CoordinatorImp coord = new CoordinatorImp();
		sf = new JAXRSServerFactoryBean();
		List<Object> providers = new ArrayList<Object>();
		providers.add(new JacksonJaxbJsonProvider());
		providers.add(new TransactionProvider());
		sf.setProviders(providers);
		sf.setResourceClasses(CoordinatorImp.class);
		sf.setResourceProvider(CoordinatorImp.class,
				new SingletonResourceProvider(coord));
		sf.setAddress(url);
        sf.create();
	}

	private void startTransactionCore() {
		Configuration.init();
	}
	
	public void stop(boolean force) {
		shutdownCxf();
		shutdownTransactionCore(force);
	}

	private void shutdownTransactionCore(boolean force) {
		Configuration.shutdown(force);
	}

	private void shutdownCxf() {
		if (sf != null) sf.getBus().shutdown(true);
		sf = null;
	}
	
	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.err.println("Required argument: URL to start on");
			System.exit(1);
		}
		Server server = startServer(args);
		System.out.println("Server running - type ENTER to terminate...");
		waitForEnter();
		System.out.println("Server stopping, waiting for ongoing transactions to finish...");
		server.stop(false);
		System.out.println("Server stopped.");
	}


	private static void waitForEnter() throws IOException {
		InputStream in = System.in;
		InputStreamReader r = new InputStreamReader(in);
		BufferedReader br = new BufferedReader(r);
		br.readLine();
	}

	private static Server startServer(String[] args) {
		String port = args[0];
		Server server = new Server("http://localhost:"+port);
		server.start();
		return server;
	}

}