//$Id: AddressingTest.java,v 1.1.1.1 2006/10/02 15:21:13 guy Exp $
//$Log: AddressingTest.java,v $
//Revision 1.1.1.1  2006/10/02 15:21:13  guy
//Import into CVS.
//
//Revision 1.1.1.1  2006/04/29 08:55:46  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:34  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:29  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:56  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:20  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.1  2005/10/18 12:41:02  guy
//Added addressing logic.
//
package com.atomikos.jaxws.wsa;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import junit.framework.TestCase;

import com.atomikos.icatch.jaxb.wsa.IncomingAddressingHeaders;
import com.atomikos.icatch.jaxb.wsa.OutgoingAddressingHeaders;


/**
 * 
 * Copyright &copy; 2005, Atomikos. All rights reserved.
 * 
 * 
 * Generic tests for the WSA utilities.
 * 
 */
public abstract class AddressingTest
extends TestCase
{

	

	private static void streamOutAndIn ( Serializable o )
	throws Exception
	{
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream ( bout );
		out.writeObject ( o );
		out.close();
		ByteArrayInputStream bin = new ByteArrayInputStream ( bout.toByteArray() );
		ObjectInputStream in = new ObjectInputStream ( bin );
		in.readObject();
		in.close();
	}
	

	


	public AddressingTest ( String name )
	{
		super ( name );
	}

	//Asserts serializability of the addressing classes
	public void testSerializable()
	throws Exception
	{
		SOAPMessageContext ctx = createContextWithIncomingAddressData (
			"http://to" , "http://targetRefPropURI" , "http://actionURI" , "http://messageID" , "http://replyTo" , "http://replyToRefPropURI" , "http://faultTo" ,
			"http://faultToRefPropURI" , "http://relatesToID"
		);
		
		IncomingAddressingHeaders inData = extractFromContext ( ctx );
		streamOutAndIn ( inData );
		OutgoingAddressingHeaders replyData = inData.createReplyAddress();
		streamOutAndIn ( replyData );
		OutgoingAddressingHeaders faultData = inData.createFaultAddress();
		streamOutAndIn ( faultData );
		
	}
	
	//assert correctness of incoming data
	public void testIncomingAddressData()
	throws Exception
	{
		String to = "http://to";
		String targetURI = "http://targetURI";
		String actionURI = "http://actionURI";
		String messageID = "http://messageID";
		String replyTo = "http://replyTo";
		String replyToRefPropURI = "http://replyToRefPropURI";
		String faultTo = "http://faultTo";
		String faultToRefPropURI = "http://faultToRefPropURI";
		String relatesTo = "http://relatesTo";
		SOAPMessageContext ctx = createContextWithIncomingAddressData ( 
			to ,  targetURI , actionURI , messageID , replyTo , replyToRefPropURI , 
			faultTo , faultToRefPropURI , relatesTo
		);
		IncomingAddressingHeaders inData = extractFromContext ( ctx );
		
		if ( ! inData.getTarget().equals ( targetURI ) ) fail ( "Wrong targetURI" );
	}
	
	public void testReplayAddressData()
	throws Exception
	{
		testReplyAddressData ( true );
	}
	
	public void testReplyAddressDataWithFault()
	throws Exception
	{
		testReplyAddressData ( false );
	}
	
	private void testReplyAddressData ( boolean trueForReplyFalseForFault )
	throws Exception
	{
		String inTo = "http://inTo";
		String inTargetURI = "http://inTargetURI";
		String inActionURI = "http://inActionURI";
		String inMessageID = "http://inMessageID";
		String inReplyTo = "http://inReplyTo";
		String inReplyToRefPropURI = "http://inReplyToRefPropURI";
		String inFaultTo = "http://inFaultTo";
		String inFaultToRefPropURI = "http://inFaultToRefPropURI";
		String inRelatesTo = "http://inRelatesTo";
		SOAPMessageContext ctx = createContextWithIncomingAddressData ( 
			inTo ,  inTargetURI , inActionURI , inMessageID , inReplyTo , inReplyToRefPropURI , 
			inFaultTo , inFaultToRefPropURI , inRelatesTo
		);
		IncomingAddressingHeaders inData = extractFromContext ( ctx );
		
		OutgoingAddressingHeaders replyData = null;
		if ( trueForReplyFalseForFault) replyData = inData.createReplyAddress();
		else replyData = inData.createFaultAddress();
		String replyTo = "http://replyTo";
		String replyToTarget = "http://replyToTarget";
		String faultTo = "http://faultTo";
		String faultToTarget = "http://faultToTarget";
		String messageID = "http://messageID";
		String action = "http://action";
		
		replyData.setAction( action );
		replyData.setFaultTo ( faultTo );
		replyData.setFaultToTarget ( faultToTarget );
		replyData.setMessageId ( messageID );
		replyData.setReplyTo ( replyTo );
		replyData.setReplyToTarget ( replyToTarget );
		
		if ( ! replyData.getAction().equals ( action ) ) fail ( "wrong action");
		if ( ! replyData.getFaultTo().equals ( faultTo ) ) fail ("wrong faultTo");
		if ( ! replyData.getFaultToTarget().equals ( faultToTarget) ) fail ( "wrong faultToTarget");
		if ( ! replyData.getMessageId().equals ( messageID ) ) fail ( "wrong messageID");
		if ( ! replyData.getReplyTo().equals ( replyTo ) ) fail ( "wrong replyTo");
		if ( ! replyData.getReplyToTarget().equals ( replyToTarget) ) fail ( "wrong replyToTarget");
		
		//next assert that conversion in context works
		Map<String,Object> replyContext = new HashMap<String,Object>();
		replyData.insertIntoRequestContext ( new TestBindingProvider ( replyContext ) );
		
		//assert that reply headers correspond to inData AND to what we set for the reply
		if ( trueForReplyFalseForFault) assertOutgoingHasTo ( replyContext , inReplyTo );
		else assertOutgoingHasTo ( replyContext , inFaultTo );
		if ( trueForReplyFalseForFault) assertOutgoingHasToRefProp ( replyContext, inReplyToRefPropURI );
		else assertOutgoingHasToRefProp ( replyContext, inFaultToRefPropURI );
		assertOutgoingHasMessageId ( replyContext , messageID );
		assertOutgoingHasRelatesTo ( replyContext , inMessageID );
		assertOutgoingHasReplyTo ( replyContext , replyTo );
		assertOutgoingHasReplyToTarget ( replyContext ,replyToTarget );
		assertOutgoingHasFaultTo ( replyContext , faultTo );
		assertOutgoingHasFaultToTarget ( replyContext , faultToTarget );
		assertOutgoingHasAction ( replyContext , action );
	}
	

	
   	protected abstract SOAPMessageContext createContextWithIncomingAddressData ( 
   		String toUri , String targetRefPropURI , String actionURI , 
   		String messageIdURI , String replyToURI , String replyToRefPropURI , 
   		String faultToURI , String faultToRefPropURI , String relatesToURI )
   		throws Exception;
   		
   	protected abstract IncomingAddressingHeaders extractFromContext ( SOAPMessageContext ctx )
   	throws Exception;

	
	protected abstract void assertOutgoingHasTo ( Map<String, Object> ctx , String toURI )
	throws Exception;
	   
	protected abstract void assertOutgoingHasToRefProp ( Map<String, Object> ctx , String refPropURI )
	throws Exception;
	
	protected abstract void assertOutgoingHasMessageId ( Map<String, Object> ctx , String id ) throws Exception;
	
	protected abstract void assertOutgoingHasRelatesTo ( Map<String, Object> ctx , String id ) throws Exception;
	
	protected abstract void assertOutgoingHasReplyTo ( Map<String, Object> ctx, String replyToURI ) throws Exception;
	
	protected abstract void assertOutgoingHasReplyToTarget ( Map<String, Object> ctx , String value ) throws Exception;
	
	protected abstract void assertOutgoingHasFaultTo ( Map<String, Object> ctx , String uri ) throws Exception;
	
	protected abstract void assertOutgoingHasFaultToTarget ( Map<String, Object> ctx , String value ) throws Exception;
	
	protected abstract void assertOutgoingHasAction ( Map<String, Object> ctx , String action ) throws Exception;
	
	
	private static class TestBindingProvider implements BindingProvider, Binding
	{
		
		private Map<String, Object> requestContext;
		private List<Handler> handlerChain;

		TestBindingProvider ( Map<String,Object> requestContext ) {
			this.requestContext = requestContext;
			this.handlerChain = new ArrayList<Handler>();
		}

		public Binding getBinding() {
			return this;
		}

		public EndpointReference getEndpointReference() {
			return null;
		}

		public <T extends EndpointReference> T getEndpointReference(
				Class<T> arg0) {
			return null;
		}

		public Map<String, Object> getRequestContext() {
			return requestContext;
		}

		public Map<String, Object> getResponseContext() {
			return null;
		}

		public String getBindingID() {
			return null;
		}

		public List<Handler> getHandlerChain() {
			return handlerChain;
		}

		public void setHandlerChain(List<Handler> list ) {
			this.handlerChain = list;
		}
		
	}
	
}
