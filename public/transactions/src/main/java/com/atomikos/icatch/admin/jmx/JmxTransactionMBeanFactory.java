/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.admin.jmx;

import java.util.ArrayList;
import java.util.List;

import com.atomikos.icatch.TxState;
import com.atomikos.icatch.admin.AdminTransaction;

public class JmxTransactionMBeanFactory {

	private static JmxTransactionMBean createMBean ( AdminTransaction tx )
	{
	    JmxTransactionMBean ret = null;
	    switch ( tx.getState () ) {
	    case IN_DOUBT:
	        ret = new JmxPreparedTransaction ( tx );
	        break;
	    case HEUR_ABORTED:
	    case HEUR_COMMITTED:
	    case HEUR_HAZARD:
	    case HEUR_MIXED:
	        ret = new JmxHeuristicTransaction ( tx );
	        break;
	    default:
	        ret = new JmxDefaultTransaction ( tx );
	        break;
	    }
	
	    return ret;
	}

	private static AdminTransaction[] filterHeuristics ( AdminTransaction[] txs )
	{
		List<AdminTransaction> ret = new ArrayList<AdminTransaction>();
		for ( int i = 0 ; i < txs.length ; i++ ) {
			AdminTransaction next = txs[i];
			if (next.getState().isOneOf(TxState.HEUR_ABORTED,TxState.HEUR_COMMITTED, TxState.HEUR_HAZARD, TxState.HEUR_MIXED) 
					|| (next.getState()== TxState.COMMITTING && next.hasExpired())) {
				ret.add ( next );
			}
		}
		return ( AdminTransaction[] ) ret.toArray ( new AdminTransaction[0] );
	}

	public static JmxTransactionMBean[] createMBeans(AdminTransaction[] transactions,
			boolean heuristicsOnly) {
		List<JmxTransactionMBean> ret = new ArrayList<JmxTransactionMBean>();
		if (heuristicsOnly) {
			transactions = filterHeuristics(transactions);
		}
		for (AdminTransaction tx : transactions) {
			ret.add(createMBean(tx));
		}
		return ( JmxTransactionMBean[] ) ret.toArray ( new JmxTransactionMBean[0] );
	}

}
