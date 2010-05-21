
package com.atomikos.icatch.jaxb.wsat.v200410;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PrepareResponse element declaration.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;element name="PrepareResponse">
 *   &lt;complexType>
 *     &lt;complexContent>
 *       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *         &lt;attribute name="vote" type="{http://schemas.xmlsoap.org/ws/2004/10/wsat}Vote" />
 *       &lt;/restriction>
 *     &lt;/complexContent>
 *   &lt;/complexType>
 * &lt;/element>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "PrepareResponse", namespace = "http://schemas.xmlsoap.org/ws/2004/10/wsat")
public class PrepareResponse {

    @XmlAttribute
    protected Vote vote;

    /**
     * Gets the value of the vote property.
     * 
     * @return
     *     possible object is
     *     {@link Vote }
     *     
     */
    public Vote getVote() {
        return vote;
    }

    /**
     * Sets the value of the vote property.
     * 
     * @param value
     *     allowed object is
     *     {@link Vote }
     *     
     */
    public void setVote(Vote value) {
        this.vote = value;
    }

}
