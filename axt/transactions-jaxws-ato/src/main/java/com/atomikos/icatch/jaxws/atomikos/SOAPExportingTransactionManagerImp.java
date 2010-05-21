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
