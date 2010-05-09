
package com.atomikos.icatch.jaxb.atomikos.v200510;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;


/**
 * <p>Java class for PropagationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PropagationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Root" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Tid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Timeout" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="Serial" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="Coordinator" type="{http://www.atomikos.com/schemas/2005/10/transactions}AddressType"/>
 *         &lt;element name="Properties" type="{http://www.atomikos.com/schemas/2005/10/transactions}PropertiesType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PropagationType", propOrder = {
    "root",
    "tid",
    "timeout",
    "serial",
    "coordinator",
    "properties"
})
public class PropagationType {

    @XmlElement(name = "Root", namespace = "http://www.atomikos.com/schemas/2005/10/transactions", required = true)
    protected String root;
    @XmlElement(name = "Tid", namespace = "http://www.atomikos.com/schemas/2005/10/transactions", required = true)
    protected String tid;
    @XmlElement(name = "Timeout", namespace = "http://www.atomikos.com/schemas/2005/10/transactions")
    protected long timeout;
    @XmlElement(name = "Serial", namespace = "http://www.atomikos.com/schemas/2005/10/transactions")
    protected boolean serial;
    @XmlElement(name = "Coordinator", namespace = "http://www.atomikos.com/schemas/2005/10/transactions", required = true)
    protected AddressType coordinator;
    @XmlElement(name = "Properties", namespace = "http://www.atomikos.com/schemas/2005/10/transactions")
    protected PropertiesType properties;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the root property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRoot() {
        return root;
    }

    /**
     * Sets the value of the root property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRoot(String value) {
        this.root = value;
    }

    /**
     * Gets the value of the tid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTid() {
        return tid;
    }

    /**
     * Sets the value of the tid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTid(String value) {
        this.tid = value;
    }

    /**
     * Gets the value of the timeout property.
     * 
     */
    public long getTimeout() {
        return timeout;
    }

    /**
     * Sets the value of the timeout property.
     * 
     */
    public void setTimeout(long value) {
        this.timeout = value;
    }

    /**
     * Gets the value of the serial property.
     * 
     */
    public boolean isSerial() {
        return serial;
    }

    /**
     * Sets the value of the serial property.
     * 
     */
    public void setSerial(boolean value) {
        this.serial = value;
    }

    /**
     * Gets the value of the coordinator property.
     * 
     * @return
     *     possible object is
     *     {@link AddressType }
     *     
     */
    public AddressType getCoordinator() {
        return coordinator;
    }

    /**
     * Sets the value of the coordinator property.
     * 
     * @param value
     *     allowed object is
     *     {@link AddressType }
     *     
     */
    public void setCoordinator(AddressType value) {
        this.coordinator = value;
    }

    /**
     * Gets the value of the properties property.
     * 
     * @return
     *     possible object is
     *     {@link PropertiesType }
     *     
     */
    public PropertiesType getProperties() {
        return properties;
    }

    /**
     * Sets the value of the properties property.
     * 
     * @param value
     *     allowed object is
     *     {@link PropertiesType }
     *     
     */
    public void setProperties(PropertiesType value) {
        this.properties = value;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     * 
     * <p>
     * the map is keyed by the name of the attribute and 
     * the value is the string value of the attribute.
     * 
     * the map returned by this method is live, and you can add new attribute
     * by updating the map directly. Because of this design, there's no setter.
     * 
     * 
     * @return
     *     always non-null
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

}
