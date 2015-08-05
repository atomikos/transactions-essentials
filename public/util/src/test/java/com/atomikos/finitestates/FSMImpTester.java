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
