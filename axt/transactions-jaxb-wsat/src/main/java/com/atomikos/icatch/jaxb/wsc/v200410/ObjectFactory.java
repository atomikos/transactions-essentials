
package com.atomikos.icatch.jaxb.wsc.v200410;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

import com.atomikos.icatch.jaxb.wsc.v200410.CoordinationContextType.Identifier;
import com.atomikos.icatch.jaxb.wsc.v200410.CreateCoordinationContextType.CurrentContext;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.atomikos.icatch.jaxb.wsc.v200410 package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _RegisterResponse_QNAME = new QName("http://schemas.xmlsoap.org/ws/2004/10/wscoor", "RegisterResponse");
    private final static QName _CreateCoordinationContextResponse_QNAME = new QName("http://schemas.xmlsoap.org/ws/2004/10/wscoor", "CreateCoordinationContextResponse");
    private final static QName _Envelope_QNAME = new QName("http://schemas.xmlsoap.org/soap/envelope/", "Envelope");
    private final static QName _ReplyTo_QNAME = new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing", "ReplyTo");
    private final static QName _Body_QNAME = new QName("http://schemas.xmlsoap.org/soap/envelope/", "Body");
    private final static QName _Header_QNAME = new QName("http://schemas.xmlsoap.org/soap/envelope/", "Header");
    private final static QName _Action_QNAME = new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing", "Action");
    private final static QName _EndpointReference_QNAME = new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing", "EndpointReference");
    private final static QName _ReplyAfter_QNAME = new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing", "ReplyAfter");
    private final static QName _RelatesTo_QNAME = new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing", "RelatesTo");
    private final static QName _MessageID_QNAME = new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing", "MessageID");
    private final static QName _To_QNAME = new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing", "To");
    private final static QName _CreateCoordinationContext_QNAME = new QName("http://schemas.xmlsoap.org/ws/2004/10/wscoor", "CreateCoordinationContext");
    private final static QName _FaultTo_QNAME = new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing", "FaultTo");
    private final static QName _Fault_QNAME = new QName("http://schemas.xmlsoap.org/soap/envelope/", "Fault");
    private final static QName _Register_QNAME = new QName("http://schemas.xmlsoap.org/ws/2004/10/wscoor", "Register");
    private final static QName _From_QNAME = new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing", "From");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.atomikos.icatch.jaxb.wsc.v200410
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CoordinationContext }
     * 
     */
    public CoordinationContext createCoordinationContext() {
        return new CoordinationContext();
    }

    /**
     * Create an instance of {@link CreateCoordinationContextResponseType }
     * 
     */
    public CreateCoordinationContextResponseType createCreateCoordinationContextResponseType() {
        return new CreateCoordinationContextResponseType();
    }

    /**
     * Create an instance of {@link ReferenceParametersType }
     * 
     */
    public ReferenceParametersType createReferenceParametersType() {
        return new ReferenceParametersType();
    }

    /**
     * Create an instance of {@link AttributedURI }
     * 
     */
    public AttributedURI createAttributedURI() {
        return new AttributedURI();
    }

    /**
     * Create an instance of {@link Body }
     * 
     */
    public Body createBody() {
        return new Body();
    }

    /**
     * Create an instance of {@link Detail }
     * 
     */
    public Detail createDetail() {
        return new Detail();
    }

    /**
     * Create an instance of {@link ReplyAfterType }
     * 
     */
    public ReplyAfterType createReplyAfterType() {
        return new ReplyAfterType();
    }

    /**
     * Create an instance of {@link EndpointReferenceType }
     * 
     */
    public EndpointReferenceType createEndpointReferenceType() {
        return new EndpointReferenceType();
    }

    /**
     * Create an instance of {@link ServiceNameType }
     * 
     */
    public ServiceNameType createServiceNameType() {
        return new ServiceNameType();
    }

    /**
     * Create an instance of {@link Identifier }
     * 
     */
    public Identifier createCoordinationContextTypeIdentifier() {
        return new Identifier();
    }

    /**
     * Create an instance of {@link RegisterType }
     * 
     */
    public RegisterType createRegisterType() {
        return new RegisterType();
    }

    /**
     * Create an instance of {@link Fault }
     * 
     */
    public Fault createFault() {
        return new Fault();
    }

    /**
     * Create an instance of {@link AttributedQName }
     * 
     */
    public AttributedQName createAttributedQName() {
        return new AttributedQName();
    }

    /**
     * Create an instance of {@link Envelope }
     * 
     */
    public Envelope createEnvelope() {
        return new Envelope();
    }

    /**
     * Create an instance of {@link CoordinationContextType }
     * 
     */
    public CoordinationContextType createCoordinationContextType() {
        return new CoordinationContextType();
    }

    /**
     * Create an instance of {@link CurrentContext }
     * 
     */
    public CurrentContext createCreateCoordinationContextTypeCurrentContext() {
        return new CurrentContext();
    }

    /**
     * Create an instance of {@link Expires }
     * 
     */
    public Expires createExpires() {
        return new Expires();
    }

    /**
     * Create an instance of {@link CreateCoordinationContextType }
     * 
     */
    public CreateCoordinationContextType createCreateCoordinationContextType() {
        return new CreateCoordinationContextType();
    }

    /**
     * Create an instance of {@link Relationship }
     * 
     */
    public Relationship createRelationship() {
        return new Relationship();
    }

    /**
     * Create an instance of {@link ReferencePropertiesType }
     * 
     */
    public ReferencePropertiesType createReferencePropertiesType() {
        return new ReferencePropertiesType();
    }

    /**
     * Create an instance of {@link RegisterResponseType }
     * 
     */
    public RegisterResponseType createRegisterResponseType() {
        return new RegisterResponseType();
    }

    /**
     * Create an instance of {@link Header }
     * 
     */
    public Header createHeader() {
        return new Header();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RegisterResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.xmlsoap.org/ws/2004/10/wscoor", name = "RegisterResponse")
    public JAXBElement<RegisterResponseType> createRegisterResponse(RegisterResponseType value) {
        return new JAXBElement<RegisterResponseType>(_RegisterResponse_QNAME, RegisterResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreateCoordinationContextResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.xmlsoap.org/ws/2004/10/wscoor", name = "CreateCoordinationContextResponse")
    public JAXBElement<CreateCoordinationContextResponseType> createCreateCoordinationContextResponse(CreateCoordinationContextResponseType value) {
        return new JAXBElement<CreateCoordinationContextResponseType>(_CreateCoordinationContextResponse_QNAME, CreateCoordinationContextResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Envelope }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.xmlsoap.org/soap/envelope/", name = "Envelope")
    public JAXBElement<Envelope> createEnvelope(Envelope value) {
        return new JAXBElement<Envelope>(_Envelope_QNAME, Envelope.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EndpointReferenceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.xmlsoap.org/ws/2004/08/addressing", name = "ReplyTo")
    public JAXBElement<EndpointReferenceType> createReplyTo(EndpointReferenceType value) {
        return new JAXBElement<EndpointReferenceType>(_ReplyTo_QNAME, EndpointReferenceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Body }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.xmlsoap.org/soap/envelope/", name = "Body")
    public JAXBElement<Body> createBody(Body value) {
        return new JAXBElement<Body>(_Body_QNAME, Body.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Header }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.xmlsoap.org/soap/envelope/", name = "Header")
    public JAXBElement<Header> createHeader(Header value) {
        return new JAXBElement<Header>(_Header_QNAME, Header.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AttributedURI }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.xmlsoap.org/ws/2004/08/addressing", name = "Action")
    public JAXBElement<AttributedURI> createAction(AttributedURI value) {
        return new JAXBElement<AttributedURI>(_Action_QNAME, AttributedURI.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EndpointReferenceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.xmlsoap.org/ws/2004/08/addressing", name = "EndpointReference")
    public JAXBElement<EndpointReferenceType> createEndpointReference(EndpointReferenceType value) {
        return new JAXBElement<EndpointReferenceType>(_EndpointReference_QNAME, EndpointReferenceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReplyAfterType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.xmlsoap.org/ws/2004/08/addressing", name = "ReplyAfter")
    public JAXBElement<ReplyAfterType> createReplyAfter(ReplyAfterType value) {
        return new JAXBElement<ReplyAfterType>(_ReplyAfter_QNAME, ReplyAfterType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Relationship }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.xmlsoap.org/ws/2004/08/addressing", name = "RelatesTo")
    public JAXBElement<Relationship> createRelatesTo(Relationship value) {
        return new JAXBElement<Relationship>(_RelatesTo_QNAME, Relationship.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AttributedURI }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.xmlsoap.org/ws/2004/08/addressing", name = "MessageID")
    public JAXBElement<AttributedURI> createMessageID(AttributedURI value) {
        return new JAXBElement<AttributedURI>(_MessageID_QNAME, AttributedURI.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AttributedURI }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.xmlsoap.org/ws/2004/08/addressing", name = "To")
    public JAXBElement<AttributedURI> createTo(AttributedURI value) {
        return new JAXBElement<AttributedURI>(_To_QNAME, AttributedURI.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreateCoordinationContextType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.xmlsoap.org/ws/2004/10/wscoor", name = "CreateCoordinationContext")
    public JAXBElement<CreateCoordinationContextType> createCreateCoordinationContext(CreateCoordinationContextType value) {
        return new JAXBElement<CreateCoordinationContextType>(_CreateCoordinationContext_QNAME, CreateCoordinationContextType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EndpointReferenceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.xmlsoap.org/ws/2004/08/addressing", name = "FaultTo")
    public JAXBElement<EndpointReferenceType> createFaultTo(EndpointReferenceType value) {
        return new JAXBElement<EndpointReferenceType>(_FaultTo_QNAME, EndpointReferenceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Fault }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.xmlsoap.org/soap/envelope/", name = "Fault")
    public JAXBElement<Fault> createFault(Fault value) {
        return new JAXBElement<Fault>(_Fault_QNAME, Fault.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RegisterType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.xmlsoap.org/ws/2004/10/wscoor", name = "Register")
    public JAXBElement<RegisterType> createRegister(RegisterType value) {
        return new JAXBElement<RegisterType>(_Register_QNAME, RegisterType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EndpointReferenceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.xmlsoap.org/ws/2004/08/addressing", name = "From")
    public JAXBElement<EndpointReferenceType> createFrom(EndpointReferenceType value) {
        return new JAXBElement<EndpointReferenceType>(_From_QNAME, EndpointReferenceType.class, null, value);
    }

}
