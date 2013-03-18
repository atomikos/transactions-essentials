package com.atomikos.persistence.imp;

import java.util.Hashtable;

import com.atomikos.finitestates.TransitionTable;
import com.atomikos.icatch.TxState;

public class TestTransitionTable implements TransitionTable<TxState> {

    public static TxState INITIAL=TxState.ACTIVE;
    public static TxState MIDDLE= TxState.COMMITTING;
    public static TxState END=TxState.COMMITTED;
    
    private Hashtable<TxState,Hashtable<TxState,Object>> table_=new Hashtable<TxState,Hashtable<TxState,Object>>();

    
    
    public TestTransitionTable() {
    	 Hashtable<TxState,Object> fromINITIAL = new Hashtable<TxState,Object>();
         fromINITIAL.put(MIDDLE , new Object());
         table_.put(INITIAL , fromINITIAL);
         
         Hashtable<TxState,Object> fromMIDDLE = new Hashtable<TxState,Object>();
         fromMIDDLE.put(END , new Object());
         table_.put(MIDDLE , fromMIDDLE);
	}
    
    
    
	public boolean legalTransition(TxState from, TxState to) {
			Hashtable<TxState,Object> fromtable = (Hashtable<TxState,Object>) table_.get(from);
	        if (fromtable ==  null) 
		  return false;
	        return (fromtable.containsKey( to ));
	    }
	}


