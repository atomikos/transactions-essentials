
package com.atomikos.icatch.jaxb.atomikos.v200510;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CommitMessageType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CommitMessageType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.atomikos.com/schemas/2005/10/transactions}TransactionMessageType">
 *       &lt;attribute name="onePhase" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CommitMessageType")
public class CommitMessageType
    extends TransactionMessageType
{

    @XmlAttribute(required = true)
    protected boolean onePhase;

    /**
     * Gets the value of the onePhase property.
     * 
     */
    public boolean isOnePhase() {
        return onePhase;
    }

    /**
     * Sets the value of the onePhase property.
     * 
     */
    public void setOnePhase(boolean value) {
        this.onePhase = value;
    }

}
