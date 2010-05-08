//$Id: FSMImpTester.java,v 1.1.1.1 2006/08/29 10:01:16 guy Exp $
//$Log: FSMImpTester.java,v $
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
//Revision 1.2  2004/10/12 13:04:22  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.1  2002/02/18 13:32:17  guy
//Added test files to package under CVS.
//

package com.atomikos.finitestates;

 /**
  *
  *
  *A tester class for FSMImp.
  */


public class FSMImpTester 
{
       public static void test() throws Exception 
    {
        FSM fsm = new FSMImp ( new TestTransitionTable() ,
			 TestTransitionTable.INITIAL);
        
        TestListener lstnr1=new TestListener();
        TestListener lstnr2=new TestListener();
        TestListener lstnr3=new TestListener();
        TestListener lstnr4=new TestListener();

        fsm.addFSMEnterListener(lstnr1, TestTransitionTable.MIDDLE);
        fsm.addFSMTransitionListener(lstnr2, TestTransitionTable.INITIAL,
			       TestTransitionTable.MIDDLE);
        fsm.addFSMPreEnterListener(lstnr3, TestTransitionTable.MIDDLE);
        fsm.addFSMPreTransitionListener(lstnr4, TestTransitionTable.INITIAL,
				TestTransitionTable.MIDDLE);
        
        try {
	  fsm.setState(TestTransitionTable.END);
	  //should cause exception since not allowed
	  throw new Exception("ERROR: transition checking not ok");
        }
        catch (IllegalStateException ill) {
        }

        fsm.setState(TestTransitionTable.MIDDLE);
        if (!lstnr1.isNotified())
	  throw new Exception("ERROR: Enter notification does not work");
        if (!lstnr2.isNotified()) 
	  throw new Exception("ERROR: transition notification not OK");
        if (!lstnr3.isNotified())
	  throw new Exception("ERROR: preenter notification not OK");
        if (!lstnr4.isNotified())
	  throw new Exception("ERROR: pretransition notification not OK");
        
        fsm.setState(TestTransitionTable.END);
    }
    
      public static void main(String[] args) 
    {
        try {
	  System.out.println("Starting: FSMImp Test.");
	  test();

        }
        catch (Exception e) {
	  System.out.println(e.getMessage());
        }
        finally {
	  System.out.println("Done:     FSMImp Test.");
        }
        
        
    }
}
