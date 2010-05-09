
package com.atomikos.icatch.jaxb.atomikos.v200510;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PrepareMessageType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PrepareMessageType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.atomikos.com/schemas/2005/10/transactions}TransactionMessageType">
 *       &lt;sequence>
 *         &lt;element name="CascadeInfo" type="{http://www.atomikos.com/schemas/2005/10/transactions}CascadeInfoType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PrepareMessageType", propOrder = {
    "cascadeInfo"
})
public class PrepareMessageType
    extends TransactionMessageType
{

    @XmlElement(name = "CascadeInfo", namespace = "http://www.atomikos.com/schemas/2005/10/transactions", required = true)
    protected List<CascadeInfoType> cascadeInfo;

    /**
     * Gets the value of the cascadeInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cascadeInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCascadeInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CascadeInfoType }
     * 
     * 
     */
    public List<CascadeInfoType> getCascadeInfo() {
        if (cascadeInfo == null) {
            cascadeInfo = new ArrayList<CascadeInfoType>();
        }
        return this.cascadeInfo;
    }

}
