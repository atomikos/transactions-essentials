
package com.atomikos.icatch.jaxb.atomikos.v200510;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.atomikos.icatch.jaxb.atomikos.v200510 package. 
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

    private final static QName _State_QNAME = new QName("http://www.atomikos.com/schemas/2005/10/transactions", "State");
    private final static QName _Rollback_QNAME = new QName("http://www.atomikos.com/schemas/2005/10/transactions", "Rollback");
    private final static QName _Prepare_QNAME = new QName("http://www.atomikos.com/schemas/2005/10/transactions", "Prepare");
    private final static QName _Forget_QNAME = new QName("http://www.atomikos.com/schemas/2005/10/transactions", "Forget");
    private final static QName _Extent_QNAME = new QName("http://www.atomikos.com/schemas/2005/10/transactions", "Extent");
    private final static QName _Propagation_QNAME = new QName("http://www.atomikos.com/schemas/2005/10/transactions", "Propagation");
    private final static QName _Commit_QNAME = new QName("http://www.atomikos.com/schemas/2005/10/transactions", "Commit");
    private final static QName _Error_QNAME = new QName("http://www.atomikos.com/schemas/2005/10/transactions", "Error");
    private final static QName _Prepared_QNAME = new QName("http://www.atomikos.com/schemas/2005/10/transactions", "Prepared");
    private final static QName _Replay_QNAME = new QName("http://www.atomikos.com/schemas/2005/10/transactions", "Replay");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.atomikos.icatch.jaxb.atomikos.v200510
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ExtentType }
     * 
     */
    public ExtentType createExtentType() {
        return new ExtentType();
    }

    /**
     * Create an instance of {@link ExtentElementType }
     * 
     */
    public ExtentElementType createExtentElementType() {
        return new ExtentElementType();
    }

    /**
     * Create an instance of {@link TransactionMessageType }
     * 
     */
    public TransactionMessageType createTransactionMessageType() {
        return new TransactionMessageType();
    }

    /**
     * Create an instance of {@link ErrorMessageType }
     * 
     */
    public ErrorMessageType createErrorMessageType() {
        return new ErrorMessageType();
    }

    /**
     * Create an instance of {@link AddressType }
     * 
     */
    public AddressType createAddressType() {
        return new AddressType();
    }

    /**
     * Create an instance of {@link PropagationType }
     * 
     */
    public PropagationType createPropagationType() {
        return new PropagationType();
    }

    /**
     * Create an instance of {@link PreparedMessageType }
     * 
     */
    public PreparedMessageType createPreparedMessageType() {
        return new PreparedMessageType();
    }

    /**
     * Create an instance of {@link PrepareMessageType }
     * 
     */
    public PrepareMessageType createPrepareMessageType() {
        return new PrepareMessageType();
    }

    /**
     * Create an instance of {@link PropertyType }
     * 
     */
    public PropertyType createPropertyType() {
        return new PropertyType();
    }

    /**
     * Create an instance of {@link CommitMessageType }
     * 
     */
    public CommitMessageType createCommitMessageType() {
        return new CommitMessageType();
    }

    /**
     * Create an instance of {@link PropertiesType }
     * 
     */
    public PropertiesType createPropertiesType() {
        return new PropertiesType();
    }

    /**
     * Create an instance of {@link StateMessageType }
     * 
     */
    public StateMessageType createStateMessageType() {
        return new StateMessageType();
    }

    /**
     * Create an instance of {@link CascadeInfoType }
     * 
     */
    public CascadeInfoType createCascadeInfoType() {
        return new CascadeInfoType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StateMessageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.atomikos.com/schemas/2005/10/transactions", name = "State")
    public JAXBElement<StateMessageType> createState(StateMessageType value) {
        return new JAXBElement<StateMessageType>(_State_QNAME, StateMessageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TransactionMessageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.atomikos.com/schemas/2005/10/transactions", name = "Rollback")
    public JAXBElement<TransactionMessageType> createRollback(TransactionMessageType value) {
        return new JAXBElement<TransactionMessageType>(_Rollback_QNAME, TransactionMessageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PrepareMessageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.atomikos.com/schemas/2005/10/transactions", name = "Prepare")
    public JAXBElement<PrepareMessageType> createPrepare(PrepareMessageType value) {
        return new JAXBElement<PrepareMessageType>(_Prepare_QNAME, PrepareMessageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TransactionMessageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.atomikos.com/schemas/2005/10/transactions", name = "Forget")
    public JAXBElement<TransactionMessageType> createForget(TransactionMessageType value) {
        return new JAXBElement<TransactionMessageType>(_Forget_QNAME, TransactionMessageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExtentType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.atomikos.com/schemas/2005/10/transactions", name = "Extent")
    public JAXBElement<ExtentType> createExtent(ExtentType value) {
        return new JAXBElement<ExtentType>(_Extent_QNAME, ExtentType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PropagationType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.atomikos.com/schemas/2005/10/transactions", name = "Propagation")
    public JAXBElement<PropagationType> createPropagation(PropagationType value) {
        return new JAXBElement<PropagationType>(_Propagation_QNAME, PropagationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CommitMessageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.atomikos.com/schemas/2005/10/transactions", name = "Commit")
    public JAXBElement<CommitMessageType> createCommit(CommitMessageType value) {
        return new JAXBElement<CommitMessageType>(_Commit_QNAME, CommitMessageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ErrorMessageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.atomikos.com/schemas/2005/10/transactions", name = "Error")
    public JAXBElement<ErrorMessageType> createError(ErrorMessageType value) {
        return new JAXBElement<ErrorMessageType>(_Error_QNAME, ErrorMessageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PreparedMessageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.atomikos.com/schemas/2005/10/transactions", name = "Prepared")
    public JAXBElement<PreparedMessageType> createPrepared(PreparedMessageType value) {
        return new JAXBElement<PreparedMessageType>(_Prepared_QNAME, PreparedMessageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TransactionMessageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.atomikos.com/schemas/2005/10/transactions", name = "Replay")
    public JAXBElement<TransactionMessageType> createReplay(TransactionMessageType value) {
        return new JAXBElement<TransactionMessageType>(_Replay_QNAME, TransactionMessageType.class, null, value);
    }

}
