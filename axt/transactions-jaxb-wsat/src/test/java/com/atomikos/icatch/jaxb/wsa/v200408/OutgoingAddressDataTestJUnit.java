package com.atomikos.icatch.jaxb.wsa.v200408;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

import junit.framework.TestCase;

import org.w3c.dom.Element;

import com.atomikos.icatch.jaxb.wsc.v200410.AttributedURI;
import com.atomikos.icatch.jaxb.wsc.v200410.EndpointReferenceType;
import com.atomikos.icatch.jaxb.wsc.v200410.ReferencePropertiesType;

public class OutgoingAddressDataTestJUnit extends TestCase {

	private static final String FAULT_TO = "faultTo";
	private static final String ACTION = "action";
	private static final String ADDRESS = "address";
	private static final String FAULT_TO_TARGET = "faultToTarget";
	private static final String REPLY_TO_TARGET = "replyToTarget";
	private static final String REPLY_TO = "replyTo";
	private static final String MSG_ID = "msgId";
	
	private OutgoingAddressingHeaders out;
	
	protected void setUp() throws Exception {
		super.setUp();
		out = new OutgoingAddressingHeaders();
	}
	
	private EndpointReferenceType createEPR ( String address , Element targetRefPropAsXMLElement ) 
	{
		EndpointReferenceType ret = new EndpointReferenceType();
		AttributedURI uri = new AttributedURI();
		uri.setValue( address );
		ret.setAddress ( uri );
		ret.setReferenceProperties( new ReferencePropertiesType() );
		ret.getReferenceProperties().getAny().add ( targetRefPropAsXMLElement );
		return ret;
	}
	
	private SOAPElement createEPRAsSoapElement(String addressValue,
			String targetElementName , String targetNsPrefix , String targetUri , String targetValue ) throws SOAPException 
	{
		//the name of the EPR element itself can be arbitrary ( coordinator, participant, ...)
		SOAPElement ret = AddressingXmlHelper.createEprWithTargetReferenceParameter ( 
				"arbitraryLocalElementName" , "arbitraryPrefix" , "http://arbitraryURI" , addressValue , targetValue );
		
		return ret;
	}
	
	public void testExtractFromEPR() throws Exception 
	{
		final String target = "http://10.0.1.200:8088/atomikos/services/wscoor/RegistrationRequesterPorthttp://10.0.1.200:8088/atomikos/services/wsat/ParticipantPort/root?id=http://tm.portal.atomikos.com/tx21";
		Element targetAsXmlElement = AddressingXmlHelper.createTargetReferenceParameter ( target );
		EndpointReferenceType epr = createEPR ( ADDRESS , targetAsXmlElement );
		
		//here we go...
		OutgoingAddressingHeaders out = OutgoingAddressingHeaders.extractFromEPR ( epr );
		//target URI must uniquely identify the EPR
		assertTrue ( out.getTargetURI().indexOf ( ADDRESS ) >= 0 );
		assertTrue ( out.getTargetURI() , out.getTargetURI().indexOf ( target ) >= 0 );
		assertEquals ( ADDRESS , out.getTo() );
		//the outgoing target must be the target specified in the EPR
		SOAPElement firstRefProperty = ( SOAPElement ) out.getRefPropertiesAsSOAPElement().getChildElements().next();
		assertEquals  ( targetAsXmlElement.getTextContent() , firstRefProperty.getTextContent() );
		//the following are null since not explicitly set by us
		assertNull ( out.getFaultTo() );
		assertNull ( out.getFaultToTarget() );
		assertNull ( out.getReplyToTarget() );
		assertNull ( out.getReplyTo() );
		assertNull ( out.getMessageId() );
	}

	
	public void testExtractFromEPRAsSoapElement() throws Exception 
	{
		final String target = "http://10.0.1.200:8088/atomikos/services/wscoor/RegistrationRequesterPorthttp://10.0.1.200:8088/atomikos/services/wsat/ParticipantPort/root?id=http://tm.portal.atomikos.com/tx21";
	
		SOAPElement eprAsSoapElement = createEPRAsSoapElement ( ADDRESS , 
				AddressingConstants.TARGET_ELEMENT_NAME , "ns1" , AddressingConstants.TARGET_NS_URI , target );
		
		//here we go...
		OutgoingAddressingHeaders out = OutgoingAddressingHeaders.extractFromEPR ( eprAsSoapElement );
		//sender URI must uniquely identify the EPR
		assertTrue ( out.getTargetURI().indexOf ( ADDRESS ) >= 0 );
		assertTrue ( out.getTargetURI() , out.getTargetURI().indexOf ( target ) >= 0 );
		assertEquals ( ADDRESS , out.getTo() );

		
		assertEquals  ( ADDRESS , out.getTo() );
		assertNull ( out.getAction() );
		String action = "action";
		out.setAction ( action );
		assertEquals ( action , out.getAction() );
		

		//the outgoing target must be the target specified in the EPR
		SOAPElement firstRefProperty = ( SOAPElement ) out.getRefParametersAsSOAPElement().getChildElements().next();
		assertEquals  ( target , firstRefProperty.getTextContent() );
		//the following are null since not explicitly set by us
		assertNull ( out.getFaultTo() );
		assertNull ( out.getFaultToTarget() );
		assertNull ( out.getReplyToTarget() );
		assertNull ( out.getReplyTo() );
		assertNull ( out.getMessageId() );
	}


	public void testIsExternalizable() throws Exception 
	{
		final String action = "action";
		final String to = "to";
		final String faultTo = "faultTo";
		final String faultToTarget = "faultToTarget";
		final String messageId = "messageId";
		final String relatesTo = "relatesTo";
		final String replyTo = "replyTo";
		final String replyToTarget = "replyToTarget";
		
		out.setAction(action);
		out.setTo ( to );
		out.setFaultTo(faultTo);
		out.setFaultToTarget(faultToTarget);
		out.setMessageId(messageId);
		out.setRelatesTo(relatesTo);
		out.setReplyTo(replyTo);
		out.setReplyToTarget(replyToTarget);
		
		SOAPElement refprops = AddressingXmlHelper.createReferenceProperties ( "target" );
		out.setRefPropertiesAsSoapElement( refprops );
		
		SOAPElement refParams = AddressingXmlHelper.createReferenceParameters ( "refParameter" );
		out.setRefParametersAsSoapElement ( refParams );
		
		OutgoingAddressingHeaders readAgain = (OutgoingAddressingHeaders) TestHelper.streamOutAndIn ( out );
		assertEquals ( out.getAction() , readAgain.getAction() );
		assertEquals ( out.getTo() , readAgain.getTo() );
		assertEquals ( out.getFaultTo() , readAgain.getFaultTo() );
		assertEquals ( out.getFaultToTarget() , readAgain.getFaultToTarget() );
		assertEquals ( out.getMessageId() , readAgain.getMessageId() );
		assertEquals ( out.getRelatesTo() , readAgain.getRelatesTo() );
		assertEquals ( out.getReplyTo() , readAgain.getReplyTo() );
		assertEquals ( out.getReplyToTarget() , readAgain.getReplyToTarget() );
		assertEquals ( out.getTargetURI() , readAgain.getTargetURI() );
		assertNotNull ( readAgain.getRefPropertiesAsSOAPElement() );
		assertNotNull ( readAgain.getRefParametersAsSOAPElement() );
	}
	
	public void testAction() {
		assertNull ( out.getAction() );
		out.setAction ( ACTION );
		assertEquals ( ACTION , out.getAction() );
	}
	
	public void testFaultTo() {
		assertNull ( out.getFaultTo() );
		out.setFaultTo ( FAULT_TO );
		assertEquals ( FAULT_TO, out.getFaultTo() );
	}
	
	public void testFaultToTarget() {
		assertNull ( out.getFaultToTarget() );
		out.setFaultToTarget ( FAULT_TO_TARGET );
		assertEquals ( FAULT_TO_TARGET , out.getFaultToTarget() );
	}
	
	public void testReplyToTarget() {
		assertNull ( out.getReplyToTarget() );
		out.setReplyToTarget ( REPLY_TO_TARGET );
		assertEquals ( REPLY_TO_TARGET , out.getReplyToTarget() );
	}
	
	public void testReplyTo() {
		assertNull ( out.getReplyTo() );
		out.setReplyTo ( REPLY_TO );
		assertEquals ( REPLY_TO , out.getReplyTo() );
	}
	
	public void testMessageId() {
		assertNull ( out.getMessageId() );
		out.setMessageId ( MSG_ID );
		assertEquals ( MSG_ID , out.getMessageId() );
	}
	
	

}
