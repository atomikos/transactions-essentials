package com.atomikos.icatch.trmi;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.atomikos.datasource.RecoverableResource;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.TSListener;
import com.atomikos.icatch.config.imp.AbstractUserTransactionService;
import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;
import com.atomikos.icatch.jaxb.atomikos.v200510.AtomikosJaxbSenderPort;
import com.atomikos.icatch.jaxb.wsat.v200410.WsatJaxbSenderPort;
import com.atomikos.icatch.jaxb.JaxbExporter;
import com.atomikos.icatch.msg.CommitServer;
import com.atomikos.icatch.msg.HttpTransport;
import com.atomikos.icatch.msg.Transport;
import com.atomikos.icatch.msg.soap.atomikos.AtomikosHttpTransport;
import com.atomikos.icatch.msg.soap.wsat.WsatHttpTransport;
import com.atomikos.icatch.system.Configuration;

 /**
  * Copyright &copy; 2006, Atomikos. All rights reserved.
  * 
  * A TSListener implementation to start the
  * necessary transports for message-based
  * two-phase commit.
  */

public class TransportTSListener implements TSListener 
{

	private static final String WSAT_TRANSPORT_RESOURCE_NAME = "com.atomikos.icatch.msg.soap.wsat";
	private static final String ATOMIKOS_TRANSPORT_RESOURCE_NAME = "com.atomikos.icatch.msg.soap.atomikos";
	private boolean registered = false;
	//to deal with recursive init callback 
	//and avoid registering twice
	
	public void init ( boolean before, 
			Properties p ) 
	{
		//avoid problems if multiple listeners are present 
		if ( Configuration.getResource ( ATOMIKOS_TRANSPORT_RESOURCE_NAME ) != null ||
		 	 Configuration.getResource ( WSAT_TRANSPORT_RESOURCE_NAME ) != null ) 
			registered = true;
		
		if ( before && !registered ) {
			registered = true;
	        String soapProtocols = AbstractUserTransactionService.getTrimmedProperty (
	                UserTransactionServiceFactory.SOAP_COMMIT_PROTOCOLS_PROPERTY_NAME, p );
	        if ( !("none".equals ( soapProtocols ) || soapProtocols == null) ) {
	            Configuration.logDebug ( "STARTING SOAP PORTS..." );
	            String trustAsString = AbstractUserTransactionService.getTrimmedProperty (
	                    UserTransactionServiceFactory.TRUST_CLIENT_TM_PROPERTY_NAME, p );
	            boolean trustClientTm = "true".equals ( trustAsString );
	            CommitServer commitServer = new CommitServer ( trustClientTm );
	            List transports = new ArrayList ();
	            
	            String portAsString = AbstractUserTransactionService.getTrimmedProperty (
	                    UserTransactionServiceFactory.SOAP_PORT_PROPERTY_NAME, p );
	            if ( portAsString == null )
	                throw new SysException (
	                        "Property not set: " +  UserTransactionServiceFactory.SOAP_PORT_PROPERTY_NAME );
	            int port = Integer.parseInt ( portAsString );
	            

	            String localPortAsString = AbstractUserTransactionService.getTrimmedProperty (
	                    UserTransactionServiceFactory.LOCAL_ENDPOINTS_PORT_PROPERTY_NAME , p );
	            if ( localPortAsString == null ) {
	                Configuration.logInfo ( "Property not set: " + 
	                UserTransactionServiceFactory.LOCAL_ENDPOINTS_PORT_PROPERTY_NAME +
	                " defaulting to: " + UserTransactionServiceFactory.SOAP_PORT_PROPERTY_NAME );
	                localPortAsString = portAsString;
	            }
	            int localPort = Integer.parseInt ( localPortAsString );
	            
	            String ip = AbstractUserTransactionService.getTrimmedProperty (
	                    UserTransactionServiceFactory.SOAP_HOST_ADDRESS_PROPERTY_NAME, p );
	            if ( ip == null )
	                throw new SysException (
	                        "Property not set: com.atomikos.icatch.soap_host_address" );

	            long timeout = (new Long ( AbstractUserTransactionService.getTrimmedProperty (
	                    AbstractUserTransactionServiceFactory.MAX_TIMEOUT_PROPERTY_NAME, p ) )).longValue ();
	            boolean useSecureHttp = Boolean.parseBoolean ( AbstractUserTransactionService.getTrimmedProperty( 
	            		UserTransactionServiceFactory.SECURE_HTTP_PROPERTY_NAME , p ) );
	            HttpTransport.initialize ( ip, port, useSecureHttp );
	            JaxbExporter.exportEndpoints ( localPort , useSecureHttp );

	            if ( soapProtocols.indexOf ( "atomikos" ) >= 0 ) {
	                AtomikosHttpTransport.init (
	                        ATOMIKOS_TRANSPORT_RESOURCE_NAME, timeout );
	                HttpTransport transport = AtomikosHttpTransport.getSingleton ();
	                transports.add ( transport );
	                Configuration.addResource ( AtomikosHttpTransport
	                        .getSingleton () );
	                new AtomikosJaxbSenderPort ( transport );
	            }
	            if ( soapProtocols.indexOf ( "wsat" ) >= 0 ) {
	                WsatHttpTransport.init ( WSAT_TRANSPORT_RESOURCE_NAME,
	                        timeout );
	                HttpTransport transport = WsatHttpTransport.getSingleton();
	                transports.add ( transport );
	                Configuration.addResource ( WsatHttpTransport.getSingleton () );
	                new WsatJaxbSenderPort ( transport );
	            }

	            // convert the list to array
	            Transport[] transportArray = (Transport[]) transports
	                    .toArray ( new Transport[0] );
	            commitServer.init ( Configuration.getTransactionService (),
	                    transportArray );
	            Configuration.logDebug ( "SOAP PORTS STARTED" );
	        }

		}

	}

	public void shutdown ( boolean before ) 
	{
		//remove to tolerate restart in same VM
		if ( !before )  {
			registered = false;
			Configuration.removeTSListener ( this );
			RecoverableResource res = WsatHttpTransport.getSingleton();
			
			if ( res != null ) Configuration.removeResource( res.getName() );
			res = AtomikosHttpTransport.getSingleton();
			if ( res != null ) Configuration.removeResource( res.getName() );
			JaxbExporter.unexportEndpoints();
		}
		

	}

}
