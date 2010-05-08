//$Id: TestTransitionTable.java,v 1.1.1.1 2006/08/29 10:01:16 guy Exp $
//$Log: TestTransitionTable.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:16  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:51  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:41  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:37  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:47:04  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:46  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.4  2005/08/09 15:24:57  guy
//Updated javadoc.
//
//Revision 1.3  2004/10/12 13:04:22  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.2  2002/01/29 11:29:58  guy
//Updated to latest state: repository seemed outdated?
//
//Revision 1.1  2001/03/08 18:18:50  pardon
//Made FSM a real state machine.
//

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
