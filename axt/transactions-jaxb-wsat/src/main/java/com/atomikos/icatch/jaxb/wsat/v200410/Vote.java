
package com.atomikos.icatch.jaxb.wsat.v200410;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;


/**
 * <p>Java class for Vote.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="Vote">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="VoteCommit"/>
 *     &lt;enumeration value="VoteRollback"/>
 *     &lt;enumeration value="VoteReadOnly"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlEnum
public enum Vote {

    @XmlEnumValue("VoteCommit")
    VOTE_COMMIT("VoteCommit"),
    @XmlEnumValue("VoteReadOnly")
    VOTE_READ_ONLY("VoteReadOnly"),
    @XmlEnumValue("VoteRollback")
    VOTE_ROLLBACK("VoteRollback");
    private final String value;

    Vote(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Vote fromValue(String v) {
        for (Vote c: Vote.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v.toString());
    }

}
