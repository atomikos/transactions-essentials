//$Id: TestListener.java,v 1.1.1.1 2006/08/29 10:01:16 guy Exp $
//$Log: TestListener.java,v $
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

/**
 *
 *
 *A test listener for FSMImp testing.
 */

public class TestListener implements FSMEnterListener, FSMPreEnterListener,
			FSMTransitionListener,
			FSMPreTransitionListener
{
    private boolean notified_=false;
    
//    public TestListener()
//    {
//
//    }
    
    public boolean isNotified()
    {
        return notified_;
    }

    public void resetNotified()
    {
        notified_=false;
    }

    public void entered (FSMEnterEvent e)
    {
        notified_=true;
    }

    public void preEnter (FSMEnterEvent e)
    {
        notified_=true;
    }

    public void beforeTransition (FSMTransitionEvent e) 
    {
        notified_=true;
    }

    public void transitionPerformed (FSMTransitionEvent e)
    {
        notified_=true;
    }

    
}
