package com.atomikos.icatch.jaxb.wsa.v200408;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import junit.framework.TestCase;

public class IncomingAddressDataTestJUnit extends TestCase 
{
	private static final String MSG_ID = "uuid:5b26e440-8909-11de-a4e0-f9bdfd248372";
	private static final String TARGET = "atomikos://jaxws-client-demo/tx0000100002";
	private static final String REPLY_TO_ADDRESS = "http://www.example.com/replyTo";
	private static final String REPLY_TO_TARGET = "http://www.example.com/replyToTarget";
	private static final String FAULT_TO_ADDRESS = "http://www.example.com/faultTo";
	private static final String FAULT_TO_TARGET = "http://www.example.com/faultToTarget";

	
	
	private SOAPMessageContext ctx;
	private IncomingAddressingHeaders in;
	
	protected void setUp() throws Exception 
	{
		super.setUp();
		ctx = new TestSOAPMessageContext();
		List<SOAPElement> headers = new ArrayList<SOAPElement>();
		
		SOAPElement msgId = AddressingXmlHelper.createMessageIdSOAPElement ( MSG_ID );
		headers.add(msgId);
		SOAPElement target = AddressingXmlHelper.createSOAPElement ( AddressingConstants.TARGET_ELEMENT_NAME , 
				AddressingConstants.TARGET_NS_PREFIX, AddressingConstants.TARGET_NS_URI , TARGET );
		headers.add ( target );
		SOAPElement replyToEPR = AddressingXmlHelper.createEprWithTargetReferenceParameter (
				AddressingConstants.WSA_REPLY_TO_EPR_LOCAL_ELEMENT_NAME , 
				AddressingConstants.WSA_NS_PREFIX , AddressingConstants.WSA_NS_URI , 
				REPLY_TO_ADDRESS , REPLY_TO_TARGET );
		headers.add( replyToEPR );
		SOAPElement faultToEPR = AddressingXmlHelper.createEprWithTargetReferenceParameter (
				AddressingConstants.WSA_FAULT_TO_EPR_LOCAL_ELEMENT_NAME , 
				AddressingConstants.WSA_NS_PREFIX , AddressingConstants.WSA_NS_URI , 
				FAULT_TO_ADDRESS , FAULT_TO_TARGET );
		headers.add ( faultToEPR );
		ctx.put( AddressingConstants.HEADERS_CONTEXT_PROPERTY_NAME , headers );
		in = IncomingAddressingHeaders.extractFromContext ( ctx );
	}
	
	public void testMessageId() 
	{
		assertEquals ( MSG_ID , in.getMessageId() );
	}
	
	public void testTarget() 
	{
		assertEquals ( TARGET , in.getTarget() );
	}

	public void testSenderURI()
	{
		final String SENDER_URI = AddressingXmlHelper.toString ( in.getReplyToEPR() );
		assertEquals ( SENDER_URI , in.getSenderURI() );
		assertTrue ( in.getSenderURI() , in.getSenderURI().indexOf ( REPLY_TO_ADDRESS ) >=0 );
		assertTrue ( in.getSenderURI() , in.getSenderURI().indexOf ( REPLY_TO_TARGET ) >=0 );
	}
	
	public void testCreateReplyAddress() throws Exception 
	{
		OutgoingAddressingHeaders out = in.createReplyAddress();
		assertEquals ( MSG_ID , out.getRelatesTo() );
		assertEquals ( REPLY_TO_ADDRESS , out.getTo() );
		assertNull ( out.getAction() );
		assertTrue ( out.getTargetURI().indexOf ( REPLY_TO_ADDRESS ) >= 0 );
		assertTrue ( out.getTargetURI().indexOf ( REPLY_TO_TARGET ) >= 0 );
	}
	
	public void testCreateFaultAddress() throws Exception 
	{
		OutgoingAddressingHeaders out = in.createFaultAddress();
		assertEquals ( MSG_ID , out.getRelatesTo() );
		assertEquals ( FAULT_TO_ADDRESS , out.getTo() );
		assertNull ( out.getAction() );
		assertTrue ( out.getTargetURI().indexOf ( FAULT_TO_ADDRESS ) >= 0 );
		assertTrue ( out.getTargetURI().indexOf ( FAULT_TO_TARGET ) >= 0 );
	}
	
	
	private static class TestSOAPMessageContext implements SOAPMessageContext
	{
		private Map<String,Object> map = new HashMap<String,Object>();

		public Object[] getHeaders(QName arg0, JAXBContext ctx, boolean allRoles) {
			return null;
		}

		public SOAPMessage getMessage() {
			return null;
		}

		public Set<String> getRoles() {
			return null;
		}

		public void setMessage(SOAPMessage msg) {
		}

		public Scope getScope(String scope) {
			return null;
		}

		public void setScope(String name, Scope scope) {
		}

		public void clear() {
			map.clear();
		}

		public boolean containsKey(Object key) {
			return map.containsKey(key);
		}

		public boolean containsValue(Object obj ) {
			return map.containsValue ( obj );
		}

		public Set<java.util.Map.Entry<String, Object>> entrySet() {
			return map.entrySet();
		}

		public Object get(Object key) {
			return map.get(key);
		}

		public boolean isEmpty() {
			return map.isEmpty();
		}

		public Set<String> keySet() {
			return map.keySet();
		}

		public Object put(String key, Object value) {
			return map.put(key, value);
		}

		public void putAll(Map<? extends String, ? extends Object> other) {
			map.putAll(other);
		}

		public Object remove(Object o) {
			return map.remove ( o );
		}

		public int size() {
			return map.size();
		}

		public Collection<Object> values() {
			return map.values();
		}
		
	}

}
