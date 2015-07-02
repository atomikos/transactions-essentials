package com.atomikos.datasource.xa;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

public class RecoveryScan {
	
	public static interface XidSelector {
		boolean selects(Xid xid);
	}
	
	public static List<Xid> recoverXids(XAResource xaResource, XidSelector selector) throws XAException {
		List<Xid> ret = new ArrayList<Xid>();

        boolean done = false;
        int flags = XAResource.TMSTARTRSCAN;
        Xid[] xidsFromLastScan = null;
        List<Xid> allRecoveredXidsSoFar = new ArrayList<Xid>();
        do {
        	xidsFromLastScan = xaResource.recover(flags);
            flags = XAResource.TMNOFLAGS;
            done = (xidsFromLastScan == null || xidsFromLastScan.length == 0);
            if (!done) {

                // TEMPTATIVELY SET done TO TRUE
                // TO TOLERATE ORACLE 8.1.7 INFINITE
                // LOOP (ALWAYS RETURNS SAME RECOVER
                // SET). IF A NEW SET OF XIDS IS RETURNED
                // THEN done WILL BE RESET TO FALSE

                done = true;
                for ( int i = 0; i < xidsFromLastScan.length; i++ ) {
                    Xid xid = new XID ( xidsFromLastScan[i] );
                    // our own XID implements equals and hashCode properly
                    if (!allRecoveredXidsSoFar.contains(xid)) {
                        // a new xid is returned -> we can not be in a recovery loop -> go on
                        allRecoveredXidsSoFar.add(xid);
                        done = false;
                        if (selector.selects(xid)) {
                        	ret.add(xid);
                        }
                    }
                }
            }
        } while (!done);
		
		return ret;
	}
}

