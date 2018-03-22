/**
 * Copyright (C) 2000-2017 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.datasource.xa;

import java.io.Serializable;

import javax.transaction.xa.Xid;

/**
 * Our Xid class with correct equals and hashCode.
 */

public class XID implements Serializable, Xid
{

	private static final long serialVersionUID = 4796496938014754464L;

	private static final int DEFAULT_FORMAT = ('A' << 24) + ('T' << 16)
			+ ('O' << 8) + 'M';
	
	// same formatID for each transaction
	// -1 for null Xid, 0 for OSI CCR and positive for proprietary format...
	
	private String cachedToStringForPerformance;
    private int formatId;
    private final byte[] branchQualifier;
    private final byte[] globalTransactionId;
    private final String branchQualifierStr;
    private final String globalTransactionIdStr;
    /**
     * Create a new instance with the resource name as branch. This is the main
     * constructor for new instances.
     *
     * @param tid
     *            The global transaction identifier.
     * @param resourceURL
     *            The name of the resource in the current configuration
     *            (UNIQUE!), needed for recovering the XIDs for this
     *            configuration. The resulting branch qualifier will ALWAYS
     *            start with the name of the resource, and this can be used
     *            during recovery to identify the XIDs to be recovered.
     */

    public XID ( String tid , String resourceURL )
    {
    	 this.formatId = DEFAULT_FORMAT;

         this.globalTransactionIdStr=tid;
         this.globalTransactionId = tid.toString ().getBytes ();
         if ( this.globalTransactionId.length > Xid.MAXGTRIDSIZE )
         	throw new RuntimeException ( "Max global tid length exceeded." );
         
        this.branchQualifierStr = resourceURL;
        this.branchQualifier = resourceURL.getBytes ();
        if ( this.branchQualifier.length > Xid.MAXBQUALSIZE )
            throw new RuntimeException (
                    "Max branch qualifier length exceeded." );

    }

    /**
     * Copy constructor needed during recovery: if the data source returns
     * inappropriate instances (that do not implement equals and hashCode) then
     * we will need this constructor.
     *
     * @param xid
     *            The xid.
     */

    public XID ( Xid xid )
    {
        this.formatId = xid.getFormatId ();
        this.globalTransactionId = xid.getGlobalTransactionId ();
        this.branchQualifier = xid.getBranchQualifier ();
        this.globalTransactionIdStr = new String(xid.getGlobalTransactionId ());
        this.branchQualifierStr= new String(xid.getBranchQualifier ());
    }

    @Override
	public int getFormatId ()
    {
        return this.formatId;
    }

    @Override
	public byte[] getBranchQualifier ()
    {
        return this.branchQualifier;
    }

    @Override
	public byte[] getGlobalTransactionId ()
    {
        return this.globalTransactionId;
    }

    @Override
	public boolean equals ( Object obj )
    {
    	if (this == obj)
			return true;
		if (obj instanceof XID) {
			XID xid = (XID) obj;
			return xid.getBranchQualifierAsString().equals(getBranchQualifierAsString()) && xid.getGlobalTransactionIdAsString().equals(getGlobalTransactionIdAsString());
		}
		return false;
    }

    @Override
	public String toString ()
    {
        if ( this.cachedToStringForPerformance == null ) {
            this.cachedToStringForPerformance = getGlobalTransactionIdAsString()
                    + getBranchQualifierAsString();
        }
        return this.cachedToStringForPerformance;
    }

	public  String getBranchQualifierAsString() {
		return this.branchQualifierStr;
	}

	public  String getGlobalTransactionIdAsString() {
		return this.globalTransactionIdStr;
	}

    @Override
	public int hashCode ()
    {
        return toString ().hashCode ();
    }
}
