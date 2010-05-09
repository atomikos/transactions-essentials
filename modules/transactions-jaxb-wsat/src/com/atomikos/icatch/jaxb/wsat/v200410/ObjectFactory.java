
package com.atomikos.icatch.jaxb.wsat.v200410;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.atomikos.icatch.jaxb.wsat.v200410 package. 
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

    private final static QName _Aborted_QNAME = new QName("http://schemas.xmlsoap.org/ws/2004/10/wsat", "Aborted");
    private final static QName _Rollback_QNAME = new QName("http://schemas.xmlsoap.org/ws/2004/10/wsat", "Rollback");
    private final static QName _ReadOnly_QNAME = new QName("http://schemas.xmlsoap.org/ws/2004/10/wsat", "ReadOnly");
    private final static QName _Envelope_QNAME = new QName("http://schemas.xmlsoap.org/soap/envelope/", "Envelope");
    private final static QName _Fault_QNAME = new QName("http://schemas.xmlsoap.org/soap/envelope/", "Fault");
    private final static QName _Replay_QNAME = new QName("http://schemas.xmlsoap.org/ws/2004/10/wsat", "Replay");
    private final static QName _Prepared_QNAME = new QName("http://schemas.xmlsoap.org/ws/2004/10/wsat", "Prepared");
    private final static QName _Committed_QNAME = new QName("http://schemas.xmlsoap.org/ws/2004/10/wsat", "Committed");
    private final static QName _Commit_QNAME = new QName("http://schemas.xmlsoap.org/ws/2004/10/wsat", "Commit");
    private final static QName _Prepare_QNAME = new QName("http://schemas.xmlsoap.org/ws/2004/10/wsat", "Prepare");
    private final static QName _Header_QNAME = new QName("http://schemas.xmlsoap.org/soap/envelope/", "Header");
    private final static QName _Body_QNAME = new QName("http://schemas.xmlsoap.org/soap/envelope/", "Body");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.atomikos.icatch.jaxb.wsat.v200410
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Envelope }
     * 
     */
    public Envelope createEnvelope() {
        return new Envelope();
    }

    /**
     * Create an instance of {@link ATAssertion }
     * 
     */
    public ATAssertion createATAssertion() {
        return new ATAssertion();
    }

    /**
     * Create an instance of {@link Header }
     * 
     */
    public Header createHeader() {
        return new Header();
    }

    /**
     * Create an instance of {@link PrepareResponse }
     * 
     */
    public PrepareResponse createPrepareResponse() {
        return new PrepareResponse();
    }

    /**
     * Create an instance of {@link Notification }
     * 
     */
    public Notification createNotification() {
        return new Notification();
    }

    /**
     * Create an instance of {@link Body }
     * 
     */
    public Body createBody() {
        return new Body();
    }

    /**
     * Create an instance of {@link ATAlwaysCapability }
     * 
     */
    public ATAlwaysCapability createATAlwaysCapability() {
        return new ATAlwaysCapability();
    }

    /**
     * Create an instance of {@link Fault }
     * 
     */
    public Fault createFault() {
        return new Fault();
    }

    /**
     * Create an instance of {@link ReplayResponse }
     * 
     */
    public ReplayResponse createReplayResponse() {
        return new ReplayResponse();
    }

    /**
     * Create an instance of {@link Detail }
     * 
     */
    public Detail createDetail() {
        return new Detail();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Notification }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.xmlsoap.org/ws/2004/10/wsat", name = "Aborted")
    public JAXBElement<Notification> createAborted(Notification value) {
        return new JAXBElement<Notification>(_Aborted_QNAME, Notification.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Notification }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.xmlsoap.org/ws/2004/10/wsat", name = "Rollback")
    public JAXBElement<Notification> createRollback(Notification value) {
        return new JAXBElement<Notification>(_Rollback_QNAME, Notification.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Notification }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.xmlsoap.org/ws/2004/10/wsat", name = "ReadOnly")
    public JAXBElement<Notification> createReadOnly(Notification value) {
        return new JAXBElement<Notification>(_ReadOnly_QNAME, Notification.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link Fault }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.xmlsoap.org/soap/envelope/", name = "Fault")
    public JAXBElement<Fault> createFault(Fault value) {
        return new JAXBElement<Fault>(_Fault_QNAME, Fault.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Notification }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.xmlsoap.org/ws/2004/10/wsat", name = "Replay")
    public JAXBElement<Notification> createReplay(Notification value) {
        return new JAXBElement<Notification>(_Replay_QNAME, Notification.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Notification }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.xmlsoap.org/ws/2004/10/wsat", name = "Prepared")
    public JAXBElement<Notification> createPrepared(Notification value) {
        return new JAXBElement<Notification>(_Prepared_QNAME, Notification.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Notification }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.xmlsoap.org/ws/2004/10/wsat", name = "Committed")
    public JAXBElement<Notification> createCommitted(Notification value) {
        return new JAXBElement<Notification>(_Committed_QNAME, Notification.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Notification }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.xmlsoap.org/ws/2004/10/wsat", name = "Commit")
    public JAXBElement<Notification> createCommit(Notification value) {
        return new JAXBElement<Notification>(_Commit_QNAME, Notification.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Notification }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.xmlsoap.org/ws/2004/10/wsat", name = "Prepare")
    public JAXBElement<Notification> createPrepare(Notification value) {
        return new JAXBElement<Notification>(_Prepare_QNAME, Notification.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link Body }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.xmlsoap.org/soap/envelope/", name = "Body")
    public JAXBElement<Body> createBody(Body value) {
        return new JAXBElement<Body>(_Body_QNAME, Body.class, null, value);
    }

}
