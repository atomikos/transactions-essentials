package com.atomikos.icatch.jaxb;


import javax.xml.ws.Endpoint;

import com.atomikos.icatch.jaxb.atomikos.v200510.CoordinatorPortTypeImpl;
import com.atomikos.icatch.jaxb.atomikos.v200510.ParticipantPortTypeImpl;
import com.atomikos.icatch.jaxb.wsa.v200408.WSAHandler;
import com.atomikos.icatch.msg.soap.atomikos.AtomikosHttpTransport;
import com.atomikos.icatch.msg.soap.wsat.WsatHttpTransport;
import com.atomikos.icatch.system.Configuration;

public class JaxbExporter {
	
	// Atomikos protocol
	private static Endpoint coordinatorEndpoint;
	private static Endpoint participantEndpoint;
	
	//WS-AT protocol
	private static Endpoint wsatParticipantEndpoint;
	private static Endpoint wsatCoordinatorEndpoint;
	private static Endpoint wsatFaultEndpoint;
	//WS-Coord, required by WS-AT
	private static Endpoint wscRegistrationCoordinatorEndpoint;
	private static Endpoint wscFaultEndpoint;
	private static Endpoint wscRegistrationRequesterEndpoint;
	
	private static Endpoint createAndPublishWsatEndpoint ( String url , Object implementor) 
	{
		Configuration.logInfo ( "Publishing web service endpoint: " + url );
		Endpoint endpoint = Endpoint.create ( implementor );
		WSAHandler.addWSAHandler ( endpoint );
		endpoint.publish ( url );
		return endpoint;
	}

	public synchronized static void unexportEndpoints() {
		if ( coordinatorEndpoint != null && coordinatorEndpoint.isPublished() ) {
			Configuration.logInfo ( "Stopping atomikos coordinator endpoint" );
			coordinatorEndpoint.stop();
		}
		if ( participantEndpoint != null && participantEndpoint.isPublished() ) {
			Configuration.logInfo ( "Stopping atomikos participant endpoint" );
			participantEndpoint.stop();
		}

		if ( wsatParticipantEndpoint != null && wsatParticipantEndpoint.isPublished() ) {
			Configuration.logInfo ( "Stopping wsat participant endpoint" );
			wsatParticipantEndpoint.stop();
		}
		if ( wsatCoordinatorEndpoint != null && wsatCoordinatorEndpoint.isPublished() ) {
			Configuration.logInfo ( "Stopping wsat coordinator endpoint" );
			wsatCoordinatorEndpoint.stop();
		}
		if ( wsatFaultEndpoint != null && wsatFaultEndpoint.isPublished() ) {
			Configuration.logInfo ( "Stopping wsat fault endpoint" );
			wsatFaultEndpoint.stop();
		}

		if ( wscRegistrationCoordinatorEndpoint != null && wscRegistrationCoordinatorEndpoint.isPublished() ) {
			Configuration.logInfo ( "Stopping wsc registration coordinator endpoint" );
			wscRegistrationCoordinatorEndpoint.stop();
		}
		if ( wscFaultEndpoint != null && wscFaultEndpoint.isPublished() ) {
			Configuration.logInfo ( "Stopping wsc fault endpoint" );
			wscFaultEndpoint.stop();
		}
		if ( wscRegistrationRequesterEndpoint != null && wscRegistrationRequesterEndpoint.isPublished() ) {
			Configuration.logInfo ( "Stopping wsc registration requester endpoint" );
			wscRegistrationRequesterEndpoint.stop();
		}
	}
	
	public synchronized static void exportEndpoints ( int port , boolean useSecureHttp ) {
		//use 0.0.0.0 to export to ALL ip addresses of this host
        String prefix = null;
        if ( useSecureHttp ) {
        	 prefix = "https://0.0.0.0:" + port;
        } else {
        	 prefix = "http://0.0.0.0:" + port;
        }
        
        Configuration.logInfo ( "Binding to port: " + port );
        
        Configuration.logInfo ( "Starting atomikos coordinator endpoint" );
        coordinatorEndpoint = Endpoint.publish ( prefix + AtomikosHttpTransport.COORDINATOR_SERVICE_PATH , new CoordinatorPortTypeImpl());
        
        Configuration.logInfo ( "Starting atomikos participant endpoint" );
        participantEndpoint = Endpoint.publish ( prefix + AtomikosHttpTransport.PARTICIPANT_SERVICE_PATH , new ParticipantPortTypeImpl());

        Configuration.logInfo ( "Starting wsat participant endpoint" );
        wsatParticipantEndpoint = createAndPublishWsatEndpoint ( prefix + WsatHttpTransport.PARTICIPANT_SERVICE_PATH , 
        		new com.atomikos.icatch.jaxb.wsat.v200410.ParticipantPortTypeImpl());
        
        Configuration.logInfo ( "Starting wsat coordinator endpoint" );
        wsatCoordinatorEndpoint = createAndPublishWsatEndpoint ( prefix + WsatHttpTransport.COORDINATOR_SERVICE_PATH , 
        		new com.atomikos.icatch.jaxb.wsat.v200410.CoordinatorPortTypeImpl());
       
        Configuration.logInfo ( "Starting wsat fault endpoint" );
        wsatFaultEndpoint = createAndPublishWsatEndpoint ( prefix + WsatHttpTransport.WSAT_FAULT_SERVICE_PATH , 
        		new com.atomikos.icatch.jaxb.wsat.v200410.FaultPortTypeImpl());

        Configuration.logInfo ( "Starting wsc registration coordinator endpoint" );
        wscRegistrationCoordinatorEndpoint = createAndPublishWsatEndpoint ( prefix + WsatHttpTransport.REGISTRATION_SERVICE_PATH , 
        		new com.atomikos.icatch.jaxb.wsc.v200410.RegistrationCoordinatorPortTypeImpl());
        
        Configuration.logInfo ( "Starting wsc fault endpoint" );
        wscFaultEndpoint = createAndPublishWsatEndpoint ( prefix + WsatHttpTransport.WSC_FAULT_SERVICE_PATH , 
        		new com.atomikos.icatch.jaxb.wsc.v200410.FaultPortTypeImpl());
        
        Configuration.logInfo ( "Starting wsc registration requester endpoint" );
        wscRegistrationRequesterEndpoint = createAndPublishWsatEndpoint ( prefix + WsatHttpTransport.REG_REQUESTER_SERVICE_PATH , 
        		new com.atomikos.icatch.jaxb.wsc.v200410.RegistrationRequesterPortTypeImpl());
	}

}
