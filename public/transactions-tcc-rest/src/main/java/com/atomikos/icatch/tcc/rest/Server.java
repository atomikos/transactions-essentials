/**
 * Copyright (C) 2000-2017 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
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

import com.atomikos.icatch.config.Configuration;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

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
			System.err.println("Required argument: Port to start on");
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
