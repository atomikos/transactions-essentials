/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
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
