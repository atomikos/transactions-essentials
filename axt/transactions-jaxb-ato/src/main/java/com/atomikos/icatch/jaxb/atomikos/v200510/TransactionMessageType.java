
package com.atomikos.icatch.jaxb.atomikos.v200510;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TransactionMessageType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TransactionMessageType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Target" type="{http://www.atomikos.com/schemas/2005/10/transactions}AddressType"/>
 *         &lt;element name="Sender" type="{http://www.atomikos.com/schemas/2005/10/transactions}AddressType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransactionMessageType", propOrder = {
    "target",
    "sender"
})
public class TransactionMessageType {

    @XmlElement(name = "Target", namespace = "http://www.atomikos.com/schemas/2005/10/transactions", required = true)
    protected AddressType target;
    @XmlElement(name = "Sender", namespace = "http://www.atomikos.com/schemas/2005/10/transactions", required = true)
    protected AddressType sender;

    /**
     * Gets the value of the target property.
     * 
     * @return
     *     possible object is
     *     {@link AddressType }
     *     
     */
    public AddressType getTarget() {
        return target;
    }

    /**
     * Sets the value of the target property.
     * 
     * @param value
     *     allowed object is
     *     {@link AddressType }
     *     
     */
    public void setTarget(AddressType value) {
        this.target = value;
    }

    /**
     * Gets the value of the sender property.
     * 
     * @return
     *     possible object is
     *     {@link AddressType }
     *     
     */
    public AddressType getSender() {
        return sender;
    }

    /**
     * Sets the value of the sender property.
     * 
     * @param value
     *     allowed object is
     *     {@link AddressType }
     *     
     */
    public void setSender(AddressType value) {
        this.sender = value;
    }

}
