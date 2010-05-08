//$Id: XID.java,v 1.2 2007/02/05 08:20:58 guy Exp $
//$Log: XID.java,v $
//Revision 1.2  2007/02/05 08:20:58  guy
//Merged in changes from 3.1.4
//
//Revision 1.1.1.1.4.1  2006/12/26 11:47:06  guy
//Cleaned up unnecessary code
//
//Revision 1.1.1.1  2006/08/29 10:01:10  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:36  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:27  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:27  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:52  guy
//Import.
//
//Revision 1.2  2006/03/15 10:31:30  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:06  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.8  2004/11/11 07:37:04  guy
//Improved toString() to optimize performance.
//
//Revision 1.7  2004/10/12 13:04:53  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.6  2002/02/27 09:13:34  guy
//Changed XID creation: seed not necessary: inside one LOCAL ct there is no
//internal parallellism -> no violations of isolation possible.
//
//Revision 1.5  2002/02/26 12:46:48  guy
//Updated comment: spelling error.
//
//Revision 1.4  2002/02/26 11:18:07  guy
//Updated to use a different seed for each XID constructed.
//Needed to make each XID unique, even if for same tid and resource.
//
//Revision 1.3  2002/02/19 14:15:51  guy
//Changed default format ID to ATOM code.
//
//Revision 1.2  2002/01/29 11:22:36  guy
//Updated CVS to latest state.
//

package com.atomikos.datasource.xa;

import java.io.Serializable;

import javax.transaction.xa.Xid;

/**
 * 
 * 
 * An adaptor class for mapping a String to Xid type.
 */

public class XID implements Serializable, Xid
{
    private String meAsString_;
    // cached result of toString(), to gain performance

    private int formatId_;
    // required for Xid implementation
    private int branchUsed_ = 0;
    // how many bytes in branch array are used
    private int globalUsed_ = 0;
    // how many bytes in global array are used
    private byte[] branchQualifier_ = new byte[Xid.MAXBQUALSIZE];
    // for Xid
    private byte[] globalTransactionId_ = new byte[Xid.MAXGTRIDSIZE];
    // for Xid
    private static final int DEFAULT_FORMAString = ('A' << 24) + ('T' << 16)
            + ('O' << 8) + 'M';

    // same formatID for each transaction
    // -1 for null Xid, 0 for OSI CCR and positive for proprietary format...

    private XID ( String tid )
    {
        formatId_ = DEFAULT_FORMAString;
        branchQualifier_[0] = 0;
        branchUsed_ = 1;
        globalTransactionId_ = tid.toString ().getBytes ();
        globalUsed_ = tid.toString ().getBytes ().length;
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
        branchQualifier_ = resourceURL.getBytes ();
        branchUsed_ = branchQualifier_.length;
        if ( branchQualifier_.length > Xid.MAXBQUALSIZE )
            throw new RuntimeException (
                    "Max branch qualifier length exceeded." );
        if ( globalUsed_ > Xid.MAXGTRIDSIZE )
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
        formatId_ = xid.getFormatId ();
        globalTransactionId_ = xid.getGlobalTransactionId ();
        globalUsed_ = xid.getGlobalTransactionId ().length;
        branchQualifier_ = xid.getBranchQualifier ();
        branchUsed_ = xid.getBranchQualifier ().length;
    }

    public int getFormatId ()
    {
        return formatId_;
    }

    public byte[] getBranchQualifier ()
    {
        return branchQualifier_;
    }

    public byte[] getGlobalTransactionId ()
    {
        return globalTransactionId_;
    }

    public boolean equals ( Object obj )
    {
        if ( obj == null || !(obj instanceof Xid) )
            return false;
        Xid xid = (Xid) obj;

        String id1 = new String ( xid.getGlobalTransactionId () ).intern ();
        String id2 = new String ( xid.getBranchQualifier () ).intern ();
        String id3 = new String ( getGlobalTransactionId () ).intern ();
        String id4 = new String ( getBranchQualifier () ).intern ();

        return (id1.intern ().equals ( id3.intern () ) && id2.intern ().equals (
                id4.intern () ));
    }

    public String toString ()
    {
        if ( meAsString_ == null ) {
            meAsString_ = new String ( getGlobalTransactionId () )
                    + new String ( getBranchQualifier () );
        }
        return meAsString_;
    }

    public int hashCode ()
    {
        return toString ().hashCode ();
    }
}
