
package com.atomikos.icatch.jaxb.atomikos.v200510;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CascadeInfoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CascadeInfoType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Participant" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;element name="InvocationCount" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CascadeInfoType", propOrder = {
    "participant",
    "invocationCount"
})
public class CascadeInfoType {

    @XmlElement(name = "Participant", namespace = "http://www.atomikos.com/schemas/2005/10/transactions", required = true)
    protected String participant;
    @XmlElement(name = "InvocationCount", namespace = "http://www.atomikos.com/schemas/2005/10/transactions", required = true)
    protected BigInteger invocationCount;

    /**
     * Gets the value of the participant property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParticipant() {
        return participant;
    }

    /**
     * Sets the value of the participant property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParticipant(String value) {
        this.participant = value;
    }

    /**
     * Gets the value of the invocationCount property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getInvocationCount() {
        return invocationCount;
    }

    /**
     * Sets the value of the invocationCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setInvocationCount(BigInteger value) {
        this.invocationCount = value;
    }

}
