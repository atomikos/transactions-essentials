//$Id: SOAPImportingTransactionManagerImp.java,v 1.1.1.1 2006/10/02 15:20:58 guy Exp $
//$Log: SOAPImportingTransactionManagerImp.java,v $
//Revision 1.1.1.1  2006/10/02 15:20:58  guy
//Import into CVS.
//
//Revision 1.1.1.1  2006/04/29 08:55:40  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:34  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:30  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:58  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:23  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.15  2005/11/19 16:56:42  guy
//Improved logging and added namespace for propagation/extent nested elements
//
//Revision 1.14  2005/11/12 13:45:45  guy
//Redesigned registration to wait for response
//
//Revision 1.13  2005/11/01 14:10:58  guy
//Updated javadoc.
//
//Revision 1.12  2005/10/24 09:50:03  guy
//Added tid of imported tx to insertExtent parameters.
//
//Revision 1.11  2005/10/21 08:16:33  guy
//Adapted to new abstract class design in propagation package.
//
//Revision 1.10  2005/10/18 12:40:09  guy
//Removed obsolete code.
//
//Revision 1.9  2005/09/19 10:35:54  guy
//Added tag support: client heuristic messages.
//
//Revision 1.8  2005/09/01 10:03:15  guy
//Corrected BUG: for compensation, the extent contained 2 participants
//instead of one.
//
//Revision 1.7  2005/08/30 12:51:46  guy
//Added logging.
//
//Revision 1.6  2005/08/27 11:28:51  guy
//Modified to use new Sender paradigm.
//
//Revision 1.5  2005/08/19 16:04:01  guy
//Debugged.
//
//Revision 1.4  2005/08/19 13:48:50  guy
//Debugged.
//
//Revision 1.3  2005/08/12 09:14:49  guy
//Corrected: new tx should be suspended.
//
//Revision 1.2  2005/08/11 09:24:34  guy
//Adapted to new import preferences.
//
//Revision 1.1  2005/08/10 09:04:45  guy
//Added interfaces.
//
//Revision 1.6  2005/08/09 15:24:09  guy
//Updated javadoc, and redesigned CompositeTransaction interface
//(eliminated TransactionControl and CompositeTerminator).
//
//Revision 1.5  2005/08/08 14:00:00  guy
//Corrected JAXB type references.
//
//Revision 1.4  2005/08/08 11:23:41  guy
//Completed implementation.
//
//Revision 1.3  2005/08/07 09:30:35  guy
//Completed implementation.
//
//Revision 1.2  2005/08/05 15:04:00  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.1.2.1  2005/08/04 13:25:45  guy
//Added Soap import/export utility classes and handlers.
//
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
