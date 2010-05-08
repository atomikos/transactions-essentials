//$Id: FSMPreTransitionEventSource.java,v 1.1.1.1 2006/08/29 10:01:15 guy Exp $
//$Log: FSMPreTransitionEventSource.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:15  guy
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
//Revision 1.1.1.1  2006/03/22 13:47:03  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:44  guy
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
//Revision 1.1  2001/02/27 17:22:27  pardon
//Added SynchToFSM.
//

package com.atomikos.finitestates;

/**
 *
 *
 *A source of PreTransitionEvents.
 */

public interface FSMPreTransitionEventSource extends Stateful
{
    
    public void addFSMPreTransitionListener(FSMPreTransitionListener l,
				    Object from, Object to);

}
