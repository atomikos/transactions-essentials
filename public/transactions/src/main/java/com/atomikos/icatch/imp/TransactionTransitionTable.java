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

package com.atomikos.icatch.imp;

import java.util.Hashtable;

import com.atomikos.finitestates.TransitionTable;
import com.atomikos.icatch.TxState;

/**
 *
 * <P>
 * A transition table for transaction <b>coordinator</b> objects.
 */

class TransactionTransitionTable implements TransitionTable<TxState>
{

    protected Hashtable transitions;

    protected static Hashtable<TxState,Hashtable<TxState,Object>> defaultTransitions;

 
    protected static Hashtable<TxState,Hashtable<TxState,Object>> defaultTransitions ()
    {
        if ( defaultTransitions == null ) {

            Hashtable<TxState,Hashtable<TxState,Object>> defaultTrans = new Hashtable<TxState,Hashtable<TxState,Object>> ();
            

            Hashtable<TxState,Object> fromActive = new Hashtable<TxState,Object> ();
            fromActive.put ( TxState.ACTIVE, new Object () );
            fromActive.put ( TxState.ABORTING, new Object () );
            fromActive.put ( TxState.COMMITTING, new Object () );
            fromActive.put ( TxState.PREPARING, new Object () );
            defaultTrans.put ( TxState.ACTIVE, fromActive );

            Hashtable<TxState,Object> fromABORTING = new Hashtable<TxState,Object> ();
            fromABORTING.put ( TxState.TERMINATED, new Object () );
            fromABORTING.put ( TxState.ABORTING, new Object () );
            fromABORTING.put ( TxState.HEUR_ABORTED, new Object () );
            fromABORTING.put ( TxState.HEUR_COMMITTED, new Object () );
            fromABORTING.put ( TxState.HEUR_MIXED, new Object () );
            fromABORTING.put ( TxState.HEUR_HAZARD, new Object () );
            defaultTrans.put ( TxState.ABORTING, fromABORTING );

            Hashtable<TxState,Object> fromPREPARING = new Hashtable<TxState,Object> ();
            fromPREPARING.put ( TxState.IN_DOUBT, new Object () );
            fromPREPARING.put ( TxState.TERMINATED, new Object () ); // readonly
            fromPREPARING.put ( TxState.ABORTING, new Object () );
            fromPREPARING.put ( TxState.COMMITTING, new Object () );
            defaultTrans.put ( TxState.PREPARING, fromPREPARING );

            Hashtable<TxState,Object> fromIN_DOUBT = new Hashtable<TxState,Object> ();
            fromIN_DOUBT.put ( TxState.COMMITTING, new Object () );
            fromIN_DOUBT.put ( TxState.ABORTING, new Object () );
            defaultTrans.put ( TxState.IN_DOUBT, fromIN_DOUBT );

            Hashtable<TxState,Object> fromCOMMING = new Hashtable<TxState,Object> ();
            fromCOMMING.put ( TxState.TERMINATED, new Object () );
            fromCOMMING.put ( TxState.HEUR_COMMITTED, new Object () );
            fromCOMMING.put ( TxState.HEUR_ABORTED, new Object () );
            fromCOMMING.put ( TxState.HEUR_MIXED, new Object () );
            fromCOMMING.put ( TxState.HEUR_HAZARD, new Object () );
            defaultTrans.put ( TxState.COMMITTING, fromCOMMING );

            Hashtable<TxState,Object> fromHEURCOMM = new Hashtable<TxState,Object> ();
            fromHEURCOMM.put ( TxState.TERMINATED, new Object () );
            defaultTrans.put ( TxState.HEUR_COMMITTED, fromHEURCOMM );

            Hashtable<TxState,Object> fromHEURMIXED = new Hashtable<TxState,Object> ();
            fromHEURMIXED.put ( TxState.HEUR_MIXED, new Object () );
            fromHEURMIXED.put ( TxState.TERMINATED, new Object () );
            defaultTrans.put ( TxState.HEUR_MIXED, fromHEURMIXED );

            Hashtable<TxState,Object> fromHEURHAZARD = new Hashtable<TxState,Object> ();
            fromHEURHAZARD.put ( TxState.HEUR_HAZARD, new Object () );
            fromHEURHAZARD.put ( TxState.TERMINATED, new Object () );
            defaultTrans.put ( TxState.HEUR_HAZARD, fromHEURHAZARD );

            Hashtable<TxState,Object> fromHEURABORTED = new Hashtable<TxState,Object> ();
            fromHEURABORTED.put ( TxState.TERMINATED, new Object () );
            fromHEURABORTED.put ( TxState.HEUR_ABORTED, new Object () );
            defaultTrans.put ( TxState.HEUR_ABORTED, fromHEURABORTED );

            Hashtable<TxState,Object> fromTERMINATED = new Hashtable<TxState,Object> ();
            fromTERMINATED.put ( TxState.TERMINATED, new Object () );
            defaultTrans.put ( TxState.TERMINATED, fromTERMINATED );
            defaultTransitions = defaultTrans;
        }
        return defaultTransitions;

    }

    public TransactionTransitionTable ()
    {
        super ();
        transitions = defaultTransitions ();
    }

    public TransactionTransitionTable ( Hashtable edges )
    {
        super ();
        transitions = edges;
    }

    public void setTransitions ( Hashtable edges )
    {
        transitions = edges;
    }

    /**
     * This method allows checking whether a transition is valid.
     *
     * @param from
     *            The start state of the transition.
     * @param to
     *            The end state of the transition.
     * @return true if the transition is allowed, false otherwise.
     */
    public boolean legalTransition ( TxState from , TxState to )
    {
        if ( transitions == null ) return false;
        if ( !transitions.containsKey ( from ) ) return false;

        else {
            Object allowedNextStates = transitions.get ( from );
            if ( !(allowedNextStates instanceof Hashtable) ) return false;

            Hashtable toStates = (Hashtable) allowedNextStates;
            if ( toStates.containsKey ( to ) ) return true;
            else return false;
        }
    }

}
