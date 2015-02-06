/**
 * Copyright (C) 2000-2010 Atomikos <info@atomikos.com>
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
 *
 *
 * An adaptor class for mapping a String to Xid type.
 */

public class XID implements Serializable, Xid
{

	private static final long serialVersionUID = 4796496938014754464L;

	private String meAsString;
    // cached result of toString(), to gain performance

    private int formatId;
    // required for Xid implementation
    private int branchUsed = 0;
    // how many bytes in branch array are used
    private int globalUsed = 0;
    // how many bytes in global array are used
    private byte[] branchQualifier = new byte[Xid.MAXBQUALSIZE];
    // for Xid
    private byte[] globalTransactionId = new byte[Xid.MAXGTRIDSIZE];
    // for Xid
    private static final int DEFAULT_FORMAString = ('A' << 24) + ('T' << 16)
            + ('O' << 8) + 'M';

    // same formatID for each transaction
    // -1 for null Xid, 0 for OSI CCR and positive for proprietary format...

    private XID ( String tid )
    {
        this.formatId = DEFAULT_FORMAString;
        this.branchQualifier[0] = 0;
        this.branchUsed = 1;
        this.globalTransactionId = tid.toString ().getBytes ();
        this.globalUsed = tid.toString ().getBytes ().length;
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
        this.branchUsed = this.branchQualifier.length;
        if ( this.branchQualifier.length > Xid.MAXBQUALSIZE )
            throw new RuntimeException (
                    "Max branch qualifier length exceeded." );
        if ( this.globalUsed > Xid.MAXGTRIDSIZE )
            throw new RuntimeException ( "Max global tid length exceeded." );

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
        this.globalUsed = xid.getGlobalTransactionId ().length;
        this.branchQualifier = xid.getBranchQualifier ();
        this.branchUsed = xid.getBranchQualifier ().length;
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
        if ( this.meAsString == null ) {
            this.meAsString = new String ( getGlobalTransactionId () )
                    + new String ( getBranchQualifier () );
        }
        return this.meAsString;
    }

    @Override
	public int hashCode ()
    {
        return toString ().hashCode ();
    }
}
