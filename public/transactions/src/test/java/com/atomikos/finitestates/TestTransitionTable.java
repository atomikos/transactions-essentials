package com.atomikos.finitestates;
import java.util.Hashtable;

import com.atomikos.icatch.TxState;

/**
 *
 *
 *A test class for testing FSMImp.
 */

public class TestTransitionTable implements TransitionTable
{
    public static TxState INITIAL=TxState.ACTIVE;
    public static TxState MIDDLE=TxState.COMMITTING;
    public static TxState END=TxState.TERMINATED;

    private Hashtable table_=new Hashtable();

    public TestTransitionTable()
    {
        Hashtable fromINITIAL = new Hashtable();
        fromINITIAL.put(MIDDLE , new Object());
        table_.put(INITIAL , fromINITIAL);
        
        Hashtable fromMIDDLE = new Hashtable();
        fromMIDDLE.put(END , new Object());
        table_.put(MIDDLE , fromMIDDLE);
        
    }

    public boolean legalTransition(TxState from , TxState to) 
    {
        Hashtable fromtable = (Hashtable) table_.get(from);
        if (fromtable ==  null) 
	  return false;
        return (fromtable.containsKey( to ));
    }
}
