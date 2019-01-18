/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
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

	private static final int DEFAULT_FORMAT = ('A' << 24) + ('T' << 16) + ('O' << 8) + 'M';
	
	// same formatID for each transaction
	// -1 for null Xid, 0 for OSI CCR and positive for proprietary format...
	
	private String cachedToStringForPerformance;
    private final int formatId;
    private final byte[] branchQualifier;
    private final byte[] globalTransactionId;
    private final String branchQualifierStr;
    private final String globalTransactionIdStr;
    private final String uniqueResourceName;
    
    /**
     * Create a new instance with the resource name as branch. This is the main
     * constructor for new instances.
     * 
     * @param tid
     * @param branchQualifier
     *
     */

    public XID (String tid, String branchQualifier, String uniqueResourceName)
    {
    	 this.formatId = DEFAULT_FORMAT;
    	 this.uniqueResourceName = uniqueResourceName;
         this.globalTransactionIdStr=tid;
         this.globalTransactionId = tid.toString ().getBytes ();
         if ( this.globalTransactionId.length > Xid.MAXGTRIDSIZE )
         	throw new RuntimeException ( "Max global tid length exceeded." );
         
        this.branchQualifierStr = branchQualifier;
        this.branchQualifier = branchQualifier.getBytes ();
        if ( this.branchQualifier.length > Xid.MAXBQUALSIZE )
            throw new RuntimeException (
                    "Max branch qualifier length exceeded." );

    }

    /**
     * Copy constructor needed during recovery: if the data source returns
     * inappropriate instances (that do not implement equals and hashCode) then
     * we will need this constructor.
     *
     */

    public XID (Xid xid)
    {
        this.formatId = xid.getFormatId ();
        this.globalTransactionId = xid.getGlobalTransactionId ();
        this.branchQualifier = xid.getBranchQualifier ();
        this.globalTransactionIdStr = new String(xid.getGlobalTransactionId ());
        this.branchQualifierStr= new String(xid.getBranchQualifier ());
        this.uniqueResourceName = null;
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
			return toString().equals(xid.toString());
		}
		return false;
    }

    @Override
	public String toString ()
    {
        if ( this.cachedToStringForPerformance == null ) {
    		String gtrid = byteArrayToHexString(getGlobalTransactionId());
    		String bqual = byteArrayToHexString(getBranchQualifier());
    		this.cachedToStringForPerformance = gtrid + ":" + bqual;
        }
        return this.cachedToStringForPerformance;
    }
    
    
    private static final byte[] HEX_CHAR_TABLE = {
    	    (byte)'0', (byte)'1', (byte)'2', (byte)'3',
    	    (byte)'4', (byte)'5', (byte)'6', (byte)'7',
    	    (byte)'8', (byte)'9', (byte)'A', (byte)'B',
    	    (byte)'C', (byte)'D', (byte)'E', (byte)'F'
    	  };    

    private static String byteArrayToHexString(byte[] raw) 
    {
    	byte[] hex = new byte[2 * raw.length];
    	int index = 0;

    	for (byte b : raw) {
    		int v = b & 0xFF;
    		hex[index++] = HEX_CHAR_TABLE[v >>> 4];
    		hex[index++] = HEX_CHAR_TABLE[v & 0xF];
    	}
    	return new String(hex);
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

	public String getUniqueResourceName() {
		return uniqueResourceName;
	}
}
