
package com.atomikos.icatch.jaxb.atomikos.v200510;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ExtentElementType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ExtentElementType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Participant" type="{http://www.atomikos.com/schemas/2005/10/transactions}AddressType"/>
 *         &lt;element name="Count" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="HeuristicInfo" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExtentElementType", propOrder = {
    "participant",
    "count",
    "heuristicInfo"
})
public class ExtentElementType {

    @XmlElement(name = "Participant", namespace = "http://www.atomikos.com/schemas/2005/10/transactions", required = true)
    protected AddressType participant;
    @XmlElement(name = "Count", namespace = "http://www.atomikos.com/schemas/2005/10/transactions")
    protected int count;
    @XmlElement(name = "HeuristicInfo", namespace = "http://www.atomikos.com/schemas/2005/10/transactions", required = true)
    protected List<String> heuristicInfo;

    /**
     * Gets the value of the participant property.
     * 
     * @return
     *     possible object is
     *     {@link AddressType }
     *     
     */
    public AddressType getParticipant() {
        return participant;
    }

    /**
     * Sets the value of the participant property.
     * 
     * @param value
     *     allowed object is
     *     {@link AddressType }
     *     
     */
    public void setParticipant(AddressType value) {
        this.participant = value;
    }

    /**
     * Gets the value of the count property.
     * 
     */
    public int getCount() {
        return count;
    }

    /**
     * Sets the value of the count property.
     * 
     */
    public void setCount(int value) {
        this.count = value;
    }

    /**
     * Gets the value of the heuristicInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the heuristicInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getHeuristicInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getHeuristicInfo() {
        if (heuristicInfo == null) {
            heuristicInfo = new ArrayList<String>();
        }
        return this.heuristicInfo;
    }

}
