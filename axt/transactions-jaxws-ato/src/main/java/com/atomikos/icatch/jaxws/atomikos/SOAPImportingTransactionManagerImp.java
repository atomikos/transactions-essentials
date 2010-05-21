package com.atomikos.icatch.jaxws.atomikos;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;

import javax.xml.ws.soap.SOAPFaultException;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import com.atomikos.diagnostics.Console;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.Propagation;
import com.atomikos.icatch.TransactionService;
import com.atomikos.icatch.jaxws.GenericSOAPImportingTransactionManager;
import com.atomikos.icatch.jaxws.SOAPImportingTransactionManager;
import com.atomikos.icatch.jta.TransactionManagerImp;
import com.atomikos.icatch.system.Configuration;



/**
 * 
 * Copyright &copy; 2005, Atomikos. All rights reserved.
 * 
 * A helper class to import an Atomikos transaction context
 * contained in an incoming SOAP request message, and to add
 * the necessary information to the corresponding
 * reply when it goes out.
 *
 * 
 */
public class SOAPImportingTransactionManagerImp 
extends GenericSOAPImportingTransactionManager
implements SOAPImportingTransactionManager
{

	/**
	 * Sets whether or not new transactions
	 * are JTA transactions or not. 
	 * Defaults to false.
	 * 
	 * @param jta 
	 */
   public void setCreateJtaTransactions ( boolean jta )
   {
	   if ( jta ) {
		   Properties p = getProperties();
		   p.setProperty ( TransactionManagerImp.JTA_PROPERTY_NAME , "true" );
	   }
   }
   
   public boolean useJta()
   {
	   return getProperties().getProperty ( TransactionManagerImp.JTA_PROPERTY_NAME ) != null;
   }
	

    /**
     * @see com.atomikos.icatch.jaxrpc.GenericSOAPImportingTransactionManager#findPropagationHeader(javax.xml.soap.SOAPMessage)
     */
    protected SOAPHeaderElement findPropagationHeader ( SOAPMessage msg ) throws SOAPException
    {
    	SOAPHeaderElement ret = null;
//		SOAPPart sp             = msg.getSOAPPart();
//		SOAPEnvelope senv       = sp.getEnvelope();
		SOAPHeader sh           = msg.getSOAPHeader();
//    	
//		Iterator iter =  sh.examineMustUnderstandHeaderElements (
//		   Utils.ATOMIKOS_ACTOR );
//		if ( iter.hasNext() ) ret = ( SOAPHeaderElement ) iter.next();
//		return ret;

		Configuration.logDebug ( "SOAPImportingTransactionManager: entering findPropagation" );
		Iterator iter =  sh.examineAllHeaderElements();
		boolean debug = Configuration.getConsole().getLevel() == Console.DEBUG;
		if ( ! iter.hasNext() ) Configuration.logDebug ( "SOAPImportingTransactionManager: no headers found!" );
		while ( iter.hasNext() && ret == null ) {
			SOAPHeaderElement next = (SOAPHeaderElement) iter.next();
			if ( debug ) Configuration.logDebug ( "SOAPImportingTransactionManager: examining header: " + next.toString() );
			if ( next.getNamespaceURI().equals ( Utils.ATOMIKOS_NAMESPACE ) ) {
				//Atomikos header; check if propagation
				if ( next.getLocalName().equals ( Utils.PROPAGATION_ELEMENT_NAME ) ) {
					ret = next;
				}
			}
		}
		Configuration.logDebug ( "SOAPImportingTransactionManager: exiting findPropagation" );
		return ret;

    }

    /**
     * @see com.atomikos.icatch.jaxrpc.GenericSOAPImportingTransactionManager#importTransactionFromHeader(javax.xml.soap.SOAPHeaderElement)
     */
    protected CompositeTransaction importTransactionFromHeader ( SOAPHeaderElement header , boolean orphanCheck , boolean heurCommit ) 
    throws SOAPFaultException, SOAPException
    {
    	CompositeTransaction ret = null;
		Propagation atoPropagation = Utils.extractPropagationFromHeader ( header );
		TransactionService ts = 
			Configuration.getTransactionService();
		ret = ts.recreateCompositeTransaction ( 
			atoPropagation , orphanCheck , heurCommit );
		if ( useJta() && ret.getProperty ( TransactionManagerImp.JTA_PROPERTY_NAME ) == null ) {
			String msg = "SOAPImportingTransactionManager: the provided propagation is incompatible with jta mode - " +
				"XA resources may experience long-lived locks!";
			Configuration.logWarning ( msg );
		}
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
		sh.extractHeaderElements (
						Utils.ATOMIKOS_ACTOR );
    }

    /**
     * @see com.atomikos.icatch.jaxrpc.GenericSOAPImportingTransactionManager#insertExtentHeader(javax.xml.soap.SOAPMessage, java.lang.String, java.lang.String, com.atomikos.icatch.HeuristicMessage[], java.util.Hashtable, long)
     */
    protected void insertExtentHeader(SOAPMessage msg, String tid , String parentTid, String rootTid, HeuristicMessage[] tags, Hashtable table, long timeout) 
    throws SOAPException
    {
       
		Utils.insertExtentHeader (  msg , parentTid , rootTid ,  tags , table );
    }
	
	

}
