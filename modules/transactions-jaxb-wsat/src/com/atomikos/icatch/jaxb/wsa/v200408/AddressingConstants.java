package com.atomikos.icatch.jaxb.wsa.v200408;

import javax.xml.namespace.QName;

public class AddressingConstants 
{

	public static final String WSA_NS_URI = "http://schemas.xmlsoap.org/ws/2004/08/addressing";
	public static final String WSA_ADDRESS_LOCAL_ELEMENT_NAME = "Address";
	public static final String WSA_ACTION_LOCAL_ELEMENT_NAME = "Action";
	public static final String WSA_REFERENCE_PROPERTIES_LOCAL_ELEMENT_NAME = "ReferenceProperties";
	public static final String WSA_REFERENCE_PARAMETERS_LOCAL_ELEMENT_NAME = "ReferenceParameters";
	public static final String WSA_NS_PREFIX = "wsa";
	public static final String WSA_REPLY_TO_EPR_LOCAL_ELEMENT_NAME = "ReplyTo";
	public static final String WSA_FAULT_TO_EPR_LOCAL_ELEMENT_NAME = "FaultTo";
	public static final String WSA_MSG_ID_LOCAL_ELEMENT_NAME = "MessageID";
	public static final String WSA_RELATES_TO_LOCAL_ELEMENT_NAME = "RelatesTo";
	public static final String WSA_FROM_LOCAL_ELEMENT_NAME = "From";
	public static final String WSA_ANONYMOUS_URI = "http://schemas.xmlsoap.org/ws/2004/08/addressing/role/anonymous";
	
	public static final String TARGET_ELEMENT_NAME = "Target";
	public static final String TARGET_NS_URI = "http://www.atomikos.com/transactions";
	public static final String TARGET_NS_PREFIX = "ns1";
	
	static final String HEADERS_CONTEXT_PROPERTY_NAME = "addressing:headers";
	static final QName WSA_ADDRESS_QNAME = new QName ( WSA_NS_URI , WSA_ADDRESS_LOCAL_ELEMENT_NAME , WSA_NS_PREFIX );
	static final QName WSA_REFERENCE_PROPERTIES_QNAME = new QName ( WSA_NS_URI , WSA_REFERENCE_PROPERTIES_LOCAL_ELEMENT_NAME , WSA_NS_PREFIX  );
	public static final QName WSA_REFERENCE_PARAMETERS_QNAME =  new QName ( WSA_NS_URI , WSA_REFERENCE_PARAMETERS_LOCAL_ELEMENT_NAME , WSA_NS_PREFIX  );;

	


	

	
}
