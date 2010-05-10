package com.atomikos.icatch.imp;

import java.util.Hashtable;

import com.atomikos.finitestates.TransitionTable;
import com.atomikos.icatch.TxState;

/**
 * 
 * <P>
 * A transition table for transaction <b>coordinator</b> objects.
 */

class TransactionTransitionTable implements TransitionTable
{
    protected Hashtable transitions;

    protected static Hashtable defaultTransitions;

    /**
     * Builds a default transition table for rootsets.
     * 
     * @return Hashtable The default table for rootsets.
     */

    protected static Hashtable defaultTransitions ()
    {
        if ( defaultTransitions == null ) {

            Hashtable defaultTrans = new Hashtable ();
            Hashtable fromInit = new Hashtable ();

            // allowed states to reach from INIT

            // fromInit.put(TxState.ACTIVE , new Object());
            // fromInit.put(TxState.ABORTING , new Object());
            // fromInit.put(TxState.MARKED_ABORT , new Object());
            // fromInit.put(TxState.LOCALLY_DONE , new Object());
            // needed during recovery of compensatables
            // defaultTrans.put(TxState.INIT , fromInit);

            // where can we go from ACTIVE?
            Hashtable fromActive = new Hashtable ();
            // //fromActive.put(TxState.LOCALLY_DONE , new Object());
            fromActive.put ( TxState.ACTIVE, new Object () );
            // fromActive.put(TxState.MARKED_ABORT , new Object());
            fromActive.put ( TxState.ABORTING, new Object () );
            fromActive.put ( TxState.COMMITTING, new Object () );
            fromActive.put ( TxState.PREPARING, new Object () );
            defaultTrans.put ( TxState.ACTIVE, fromActive );

            // Hashtable fromMarkedAbort=new Hashtable();
            // fromMarkedAbort.put(TxState.ABORTING , new Object());
            // fromMarkedAbort.put(TxState.MARKED_ABORT , new Object());
            // defaultTrans.put(TxState.MARKED_ABORT , fromMarkedAbort);

            // //Hashtable fromLD=new Hashtable();
            // //fromLD.put(TxState.ACTIVE , new Object());
            // can become active again if new sibling arrives
            // //fromLD.put(TxState.ABORTING , new Object());
            // //fromLD.put(TxState.PREPARING , new Object());
            // //fromLD.put(TxState.COMMITTING , new Object());
            // //fromLD.put(TxState.LOCALLY_DONE , new Object());
            // fromLD.put(TxState.MARKED_ABORT , new Object());
            // //defaultTrans.put(TxState.LOCALLY_DONE , fromLD);

            Hashtable fromABORTING = new Hashtable ();
            fromABORTING.put ( TxState.TERMINATED, new Object () );
            fromABORTING.put ( TxState.ABORTING, new Object () );
            fromABORTING.put ( TxState.HEUR_ABORTED, new Object () );
            fromABORTING.put ( TxState.HEUR_COMMITTED, new Object () );
            fromABORTING.put ( TxState.HEUR_MIXED, new Object () );
            fromABORTING.put ( TxState.HEUR_HAZARD, new Object () );
            defaultTrans.put ( TxState.ABORTING, fromABORTING );

            Hashtable fromPREPARING = new Hashtable ();
            fromPREPARING.put ( TxState.IN_DOUBT, new Object () );
            fromPREPARING.put ( TxState.TERMINATED, new Object () ); // readonly
            fromPREPARING.put ( TxState.ABORTING, new Object () );
            // fromPREPARING.put(TxState.HEUR_MIXED , new Object());
            // fromPREPARING.put(TxState.HEUR_HAZARD , new Object());
            fromPREPARING.put ( TxState.COMMITTING, new Object () ); // SYNCH
            // needed for beforeCompletion notification of 1PC
            defaultTrans.put ( TxState.PREPARING, fromPREPARING );

            Hashtable fromIN_DOUBT = new Hashtable ();
            fromIN_DOUBT.put ( TxState.COMMITTING, new Object () );
            fromIN_DOUBT.put ( TxState.ABORTING, new Object () );
            defaultTrans.put ( TxState.IN_DOUBT, fromIN_DOUBT );

            Hashtable fromCOMMING = new Hashtable ();
            // fromCOMMING.put(TxState.LOCALLY_DONE , new Object());
            // to allow failed commits to rollback later!
            // fromCOMMING.put(TxState.COMMITTED , new Object());
            fromCOMMING.put ( TxState.TERMINATED, new Object () );
            // allowed in 1-phase commit: can fail in RM
            // fromCOMMING.put(TxState.HEUR_COMMITTED , new Object());
            fromCOMMING.put ( TxState.HEUR_COMMITTED, new Object () );
            fromCOMMING.put ( TxState.HEUR_ABORTED, new Object () );
            fromCOMMING.put ( TxState.HEUR_MIXED, new Object () );
            fromCOMMING.put ( TxState.HEUR_HAZARD, new Object () );
            defaultTrans.put ( TxState.COMMITTING, fromCOMMING );

            Hashtable fromHEURCOMM = new Hashtable ();
            fromHEURCOMM.put ( TxState.TERMINATED, new Object () );
            // fromCOMM.put(TxState.COMMITTED , new Object());
            defaultTrans.put ( TxState.HEUR_COMMITTED, fromHEURCOMM );

            Hashtable fromHEURMIXED = new Hashtable ();
            fromHEURMIXED.put ( TxState.HEUR_MIXED, new Object () );
            // idempotence for logging of hazards!
            fromHEURMIXED.put ( TxState.TERMINATED, new Object () );
            defaultTrans.put ( TxState.HEUR_MIXED, fromHEURMIXED );

            Hashtable fromHEURHAZARD = new Hashtable ();
            fromHEURHAZARD.put ( TxState.HEUR_HAZARD, new Object () );
            // idempotent for indoubt resoltion and logging!
            fromHEURHAZARD.put ( TxState.TERMINATED, new Object () );
            defaultTrans.put ( TxState.HEUR_HAZARD, fromHEURHAZARD );

            Hashtable fromHEURABORTED = new Hashtable ();
            fromHEURABORTED.put ( TxState.TERMINATED, new Object () );
            fromHEURABORTED.put ( TxState.HEUR_ABORTED, new Object () );
            defaultTrans.put ( TxState.HEUR_ABORTED, fromHEURABORTED );

            Hashtable fromTERMINATED = new Hashtable ();
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
    public boolean legalTransition ( Object from , Object to )
    {
        if ( transitions == null )
            return false;
        if ( !transitions.containsKey ( from ) )
            return false;
        // if not explicitly allowed -> deny.
        else {
            Object allowedNextStates = transitions.get ( from );
            if ( !(allowedNextStates instanceof Hashtable) )
                return false;
            // if not valid transition table -> same as not specified -> deny.

            Hashtable toStates = (Hashtable) allowedNextStates;
            // this will work if we are here!

            if ( toStates.containsKey ( to ) )
                return true;
            else
                return false;
        }// else
    }

}
