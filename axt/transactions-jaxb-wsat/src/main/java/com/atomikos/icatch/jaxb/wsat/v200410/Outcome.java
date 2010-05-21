
package com.atomikos.icatch.jaxb.wsat.v200410;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;


/**
 * <p>Java class for Outcome.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="Outcome">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Commit"/>
 *     &lt;enumeration value="Rollback"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlEnum
public enum Outcome {

    @XmlEnumValue("Commit")
    COMMIT("Commit"),
    @XmlEnumValue("Rollback")
    ROLLBACK("Rollback");
    private final String value;

    Outcome(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Outcome fromValue(String v) {
        for (Outcome c: Outcome.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v.toString());
    }

}
