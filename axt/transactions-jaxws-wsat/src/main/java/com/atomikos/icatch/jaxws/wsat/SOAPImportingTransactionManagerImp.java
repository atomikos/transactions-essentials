package com.atomikos.icatch.jaxws.wsat;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;

import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.soap.SOAPFaultException;

import com.atomikos.diagnostics.Console;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.Propagation;
import com.atomikos.icatch.TransactionService;
import com.atomikos.icatch.jaxb.wsa.TransactionScopedAddressMapEntry;
import com.atomikos.icatch.jaxb.wsa.v200408.OutgoingAddressingHeaders;
import com.atomikos.icatch.jaxb.wsc.v200410.Utils;
import com.atomikos.icatch.jaxws.GenericSOAPImportingTransactionManager;
import com.atomikos.icatch.jta.TransactionManagerImp;
import com.atomikos.icatch.msg.CommitServer;
import com.atomikos.icatch.msg.soap.wsat.WsatHttpTransport;
import com.atomikos.icatch.system.Configuration;

/**
 * 
 * Copyright &copy; 2005, Atomikos. All rights reserved.
 * 
 * A helper class to import a WS-AtomicTransaction context
 * contained in an incoming SOAP request message, and to add
 * the necessary information to the corresponding
 * reply when it goes out.
 *
 * 
 */
public class SOAPImportingTransactionManagerImp
    extends GenericSOAPImportingTransactionManager
{

	private long defaultTimeout;
	
	//map must be static because different instances may be used for request
	//and response chain!!!
	private static HashMap tidToAddressMap = new HashMap();
	
	public SOAPImportingTransactionManagerImp ( long defaultTimeout )
	{
		this.defaultTimeout = defaultTimeout;
		Properties p = getProperties();
		p.setProperty ( TransactionManagerImp.JTA_PROPERTY_NAME , "true" );
	}

	/**
	 * @see com.atomikos.icatch.jaxrpc.GenericSOAPImportingTransactionManager#findPropagationHeader(javax.xml.soap.SOAPMessage)
	 */
	protected SOAPHeaderElement findPropagationHeader ( SOAPMessage msg ) throws SOAPException
	{
		if ( Configuration.getConsole().getLevel() == Console.DEBUG ) {
			try {
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				msg.writeTo ( bout );
				bout.close();
				Configuration.logDebug ( "Looking for transaction context in message: " + bout.toString() );
			} catch ( IOException e ) {
				Configuration.logDebug ( "Could not log message" , e );
			}
		}
		return Utils.findPropagationHeader ( msg , Utils.WSAT_TYPE_URI );
	}

	/**
	 * @see com.atomikos.icatch.jaxrpc.GenericSOAPImportingTransactionManager#importTransactionFromHeader(javax.xml.soap.SOAPHeaderElement)
	 */
	protected CompositeTransaction importTransactionFromHeader ( SOAPHeaderElement header , boolean orphanCheck , boolean heurCommit ) 
	throws SOAPFaultException, SOAPException
	{
		CompositeTransaction ret = null;
		Propagation atoPropagation = Utils.extractPropagationFromHeader ( header , Utils.WSAT_TYPE_URI , WsatHttpTransport.getSingleton() , defaultTimeout );
		TransactionService ts = 
			Configuration.getTransactionService();
		if ( atoPropagation != null ) 
			ret = ts.recreateCompositeTransaction ( 
			atoPropagation , orphanCheck , heurCommit );
		else {
			Configuration.logWarning ( "SOAPImportingTransactionManager: Invalid context header" );
			logIfDebug ( header );
			throw new SOAPException ( "Invalid context header");
		}
		OutgoingAddressingHeaders regAddress = Utils.extractRegistrationServiceAddress ( header );
		if ( ret != null ) {
			new TransactionScopedAddressMapEntry ( ret , regAddress , tidToAddressMap );
		}
		else {
			Configuration.logWarning ( "SOAPImportingTransactionManager: Missing context content: registration service" );
			logIfDebug ( header );
			throw new SOAPException ( "Missing context content: registration service" );
		} 
		 
        
        //mark tx as JTA compliant
        ret.setProperty ( TransactionManagerImp.JTA_PROPERTY_NAME , "true" );
        
		return ret;
	}

	/**
	 * @see com.atomikos.icatch.jaxrpc.GenericSOAPImportingTransactionManager#removePropagationHeader(javax.xml.soap.SOAPMessage)
	 */
	protected void removePropagationHeader ( SOAPMessage msg ) throws SOAPException
	{
		SOAPPart sp             = msg.getSOAPPart();
		SOAPEnvelope senv       = sp.getEnvelope();
		SOAPHeader sh           = senv.getHeader();
		
		SOAPHeaderElement header = findPropagationHeader ( msg );
		if ( header != null ) header.detachNode();
	}

	/**
	 * @see com.atomikos.icatch.jaxrpc.GenericSOAPImportingTransactionManager#insertExtentHeader(javax.xml.soap.SOAPMessage, java.lang.String, java.lang.String, com.atomikos.icatch.HeuristicMessage[], java.util.Hashtable, long)
	 */
	protected void insertExtentHeader(
		SOAPMessage msg, String tid , String parentTid, 
		String rootTid, HeuristicMessage[] tags, Hashtable table, 
		long timeout) 
	throws SOAPException
	{
       
        //we may register twice for siblings, but this should not be a problem 
        //even if not allowed by WS-T, we ignore the response?
		WsatHttpTransport transport =  (WsatHttpTransport) WsatHttpTransport.getSingleton();
		String localParticipantAddress = transport.getParticipantAddress();
		
		String localParticipantURI =  CommitServer.createGlobalUri ( localParticipantAddress , rootTid );
		OutgoingAddressingHeaders regAddress = TransactionScopedAddressMapEntry.extractAddressFromMap ( tid , tidToAddressMap );
		try
        {
            if ( regAddress != null ) {
            	String registrationRequesterAddress = transport.getRegistrationRequesterServiceURL();
            	String registrationFaultAddress = transport.getWscFaultServiceURL() ;
            	Utils.registerAsParticipant ( regAddress , registrationRequesterAddress , localParticipantURI , transport , timeout );
            	Configuration.logDebug ( "SOAPImportingTransactionManager: registered as subordinate for imported coordinator " + rootTid );
			}
            else {
            	//can happen if tx times out in between?
            	Configuration.logDebug ( "SOAPImportingTransactionManager: NO REGISTRATION ADDRESS FOR IMPORTED TRANSACTION: " + tid );
            	throw new SOAPException ( "SOAPImportingTransactionManager: NO REGISTRATION ADDRESS FOR IMPORTED TRANSACTION: " + tid );
            } 
        } catch ( Exception e )
        {
            Configuration.logWarning ( "SOAPImportingTransactionManager: error during registration" , e );
            throw new SOAPException ( e );
        }
	}




}
