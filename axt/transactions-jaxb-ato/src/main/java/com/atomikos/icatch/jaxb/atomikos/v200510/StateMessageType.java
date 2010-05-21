
package com.atomikos.icatch.jaxb.atomikos.v200510;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for StateMessageType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="StateMessageType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.atomikos.com/schemas/2005/10/transactions}TransactionMessageType">
 *       &lt;attribute name="committed" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StateMessageType")
public class StateMessageType
    extends TransactionMessageType
{

    @XmlAttribute
    protected Boolean committed;

    /**
     * Gets the value of the committed property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCommitted() {
        return committed;
    }

    /**
     * Sets the value of the committed property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCommitted(Boolean value) {
        this.committed = value;
    }

}
