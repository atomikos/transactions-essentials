package com.atomikos.finitestates;
import java.util.Hashtable;

/**
 *
 *
 *A test class for testing FSMImp.
 */

public class TestTransitionTable implements TransitionTable
{
    public static Object INITIAL=new String("INITIAL");
    public static Object MIDDLE=new String("MIDDLE STATE");
    public static Object END=new String ("END STATE");

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

    public boolean legalTransition(Object from , Object to) 
    {
        Hashtable fromtable = (Hashtable) table_.get(from);
        if (fromtable ==  null) 
	  return false;
        return (fromtable.containsKey( to ));
    }
}
