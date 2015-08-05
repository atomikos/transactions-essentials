/**
 * Copyright (C) 2000-2015 Atomikos <info@atomikos.com>
 *
 * This code ("Atomikos TransactionsEssentials"), by itself,
 * is being distributed under the
 * Apache License, Version 2.0 ("License"), a copy of which may be found at
 * http://www.atomikos.com/licenses/apache-license-2.0.txt .
 * You may not use this file except in compliance with the License.
 *
 * While the License grants certain patent license rights,
 * those patent license rights only extend to the use of
 * Atomikos TransactionsEssentials by itself.
 *
 * This code (Atomikos TransactionsEssentials) contains certain interfaces
 * in package (namespace) com.atomikos.icatch
 * (including com.atomikos.icatch.Participant) which, if implemented, may
 * infringe one or more patents held by Atomikos.
 * It should be appreciated that you may NOT implement such interfaces;
 * licensing to implement these interfaces must be obtained separately from Atomikos.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package com.atomikos.datasource.xa;

import java.io.Serializable;
import java.util.Arrays;

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
    private byte[] branchQualifier = new byte[Xid.MAXBQUALSIZE];
    private byte[] globalTransactionId = new byte[Xid.MAXGTRIDSIZE];

    private XID ( String tid )
    {
        this.formatId = DEFAULT_FORMAT;
        this.branchQualifier[0] = 0;
        this.globalTransactionId = tid.toString ().getBytes ();
        if ( this.globalTransactionId.length > Xid.MAXGTRIDSIZE )
        	throw new RuntimeException ( "Max global tid length exceeded." );
    }

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
        this ( tid );
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
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
        Xid xid = (Xid) obj;
        return Arrays.equals(xid.getGlobalTransactionId (),getGlobalTransactionId ()) && Arrays.equals(xid.getBranchQualifier (), getBranchQualifier ());

    }

    @Override
	public String toString ()
    {
        if ( this.cachedToStringForPerformance == null ) {
            this.cachedToStringForPerformance = getGlobalTransactionIdAsString(this)
                    + getBranchQualifierAsString(this);
        }
        return this.cachedToStringForPerformance;
    }

	public static String getBranchQualifierAsString(Xid xid) {
		return new String ( xid.getBranchQualifier() );
	}

	public static String getGlobalTransactionIdAsString(Xid xid) {
		return new String ( xid.getGlobalTransactionId() );
	}

    @Override
	public int hashCode ()
    {
        return toString ().hashCode ();
    }
}
