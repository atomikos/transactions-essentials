//$Id: SOAPExportingTransactionManagerImp.java,v 1.1.1.1 2006/10/02 15:20:58 guy Exp $
//$Log: SOAPExportingTransactionManagerImp.java,v $
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
//Revision 1.2  2006/03/21 13:23:51  guy
//Introduced active recovery and CompTx properties as meta-tags.
//
//Revision 1.1.1.1  2006/03/09 14:59:23  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.6  2005/10/21 08:16:33  guy
//Adapted to new abstract class design in propagation package.
//
//Revision 1.5  2005/08/27 11:28:51  guy
//Modified to use new Sender paradigm.
//
//Revision 1.4  2005/08/23 13:29:42  guy
//Added logging to configuration.
//
//Revision 1.3  2005/08/19 13:48:50  guy
//Debugged.
//
//Revision 1.2  2005/08/11 09:24:34  guy
//Adapted to new import preferences.
//
//Revision 1.1  2005/08/10 09:04:45  guy
//Added interfaces.
//
//Revision 1.4  2005/08/08 14:00:00  guy
//Corrected JAXB type references.
//
//Revision 1.3  2005/08/08 11:23:41  guy
//Completed implementation.
//
//Revision 1.2  2005/08/05 15:04:00  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.1.2.1  2005/08/04 13:25:45  guy
//Added Soap import/export utility classes and handlers.
//
package com.atomikos.icatch.jaxws.atomikos;

import java.util.Iterator;

import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.Extent;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.imp.ExtentImp;
import com.atomikos.icatch.jaxws.SOAPExportingTransactionManager;
import com.atomikos.icatch.msg.soap.atomikos.AtomikosHttpTransport;
import com.atomikos.icatch.system.Configuration;

/**
 * 
 * Copyright &copy; 2005, Atomikos. All rights reserved.
 * 
 * A helper class to export a transaction via SOAP.
 * The application can construct an instance of 
 * this class to add the Atomikos propagation information  
 * of the current thread's transaction to an
 * outgoing SOAP message, and to extract the
 * remote transaction information for the response.
 * 
 */

public class SOAPExportingTransactionManagerImp implements SOAPExportingTransactionManager
{	

    
	/**
	 * @see SOAPExportingTransactionManager
	 */
             
	public void extractExtent ( SOAPMessage msg )
	throws SOAPException,
	RollbackException
	{
		
		SOAPPart sp             = msg.getSOAPPart();
		SOAPEnvelope senv       = sp.getEnvelope();
		SOAPHeader sh           = senv.getHeader();
		Extent extent = new ExtentImp();
		String tid = null;
		Iterator iter =  sh.examineHeaderElements (
		   Utils.ATOMIKOS_ACTOR );
		if ( iter.hasNext() )
		{
		   SOAPHeaderElement extentHeader = ( SOAPHeaderElement ) iter.next();
		   
		   tid = Utils.extractExtentFromHeader ( extentHeader , extent );	   			 	
		   		
		   

		}
		else {
			throw new SOAPException ( "No extent found in message");
		}
		
		
		CompositeTransactionManager ctm =
			Configuration.getCompositeTransactionManager();

		CompositeTransaction tx =
			ctm.getCompositeTransaction ( tid );
		if ( tx == null )
			throw new RollbackException ( tid );
			
			
		tx.getTransactionControl().getExtent().add ( extent );
			 
			 
		//remove processed headers of this actor
		sh.extractHeaderElements (
		   Utils.ATOMIKOS_ACTOR );
		
	}
    
    
	 /**
	  * @see SOAPExportingTransactionManager
	  */
      
	public void insertPropagation ( 
		String tid ,
		SOAPMessage msg )
		throws RollbackException, SysException, SOAPException
	{
		
		CompositeTransactionManager ctm =
					Configuration.getCompositeTransactionManager();
		if ( ctm == null ) throw new SOAPException ( "Transaction service not initialized?" );
		CompositeTransaction tx =
					ctm.getCompositeTransaction ( tid );
		if ( tx == null )
			throw new RollbackException ( tid );
			
		
		if ( AtomikosHttpTransport.getSingleton() == null ) throw new SOAPException ( "No transport found" );
		String coordinatorAddress = AtomikosHttpTransport.getSingleton().getCoordinatorAddress();
		Utils.insertPropagationHeader ( msg , tx.getCompositeCoordinator().getCoordinatorId() ,
				tid , tx.isSerial() , tx.getTimeout() , coordinatorAddress , 
				tx.getCompositeCoordinator().getCoordinatorId() , tx.getProperties()
		);
		
		
	}

}
