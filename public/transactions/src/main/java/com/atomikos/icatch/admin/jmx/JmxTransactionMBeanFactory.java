package com.atomikos.icatch.admin.jmx;

import java.util.ArrayList;
import java.util.List;

import com.atomikos.icatch.admin.AdminTransaction;

public class JmxTransactionMBeanFactory {

	private static JmxTransactionMBean createMBean ( AdminTransaction tx )
	{
	    JmxTransactionMBean ret = null;
	    switch ( tx.getState () ) {
	    case AdminTransaction.STATE_PREPARED:
	        ret = new JmxPreparedTransaction ( tx );
	        break;
	    case AdminTransaction.STATE_HEUR_ABORTED:
	        ret = new JmxHeuristicTransaction ( tx );
	        break;
	    case AdminTransaction.STATE_HEUR_COMMITTED:
	        ret = new JmxHeuristicTransaction ( tx );
	        break;
	    case AdminTransaction.STATE_HEUR_HAZARD:
	        ret = new JmxHeuristicTransaction ( tx );
	        break;
	    case AdminTransaction.STATE_HEUR_MIXED:
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
			switch ( next.getState() ) {
				case AdminTransaction.STATE_HEUR_ABORTED:
					ret.add ( next );
					break;
				case AdminTransaction.STATE_HEUR_COMMITTED:
					ret.add ( next );
					break;
				case AdminTransaction.STATE_HEUR_HAZARD:
					ret.add ( next );
					break;
				case AdminTransaction.STATE_HEUR_MIXED:
					ret.add ( next );
					break;
				default: break;
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
