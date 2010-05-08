//$Id: StateDescriptor.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//$Log: StateDescriptor.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:05  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:37  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:27  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:27  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:53  guy
//Import.
//
//Revision 1.2  2006/03/15 10:31:35  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:07  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.4  2005/08/05 15:03:34  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.3  2004/10/12 13:03:30  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.2  2004/03/22 15:37:33  guy
//Merged-in changes from branch redesign-4-2003.
//
//Revision 1.1.10.1  2003/06/20 16:31:37  guy
//*** empty log message ***
//
//Revision 1.1  2002/03/10 12:48:46  guy
//Updated admintool facility to use lists for all messages.
//
//Revision 1.1  2002/03/09 23:51:07  guy
//Added provisions for improved display of AdminTool.
//

package com.atomikos.icatch.admin.imp;

import com.atomikos.icatch.HeuristicMessage;

/**
 * 
 * 
 * A descriptor class containing state, message pairs.
 */

class StateDescriptor
{
    /**
     * The object denoting the state.
     */

    public Object state;

    /**
     * The heuristic message that goes with it.
     */

    public HeuristicMessage[] messages;

    /**
     * Create a new instance.
     * 
     * @param state
     *            The state.
     * @param messages
     *            The messages describing the work done.
     */

    public StateDescriptor ( Object state , HeuristicMessage[] messages )
    {
        this.state = state;
        this.messages = messages;
    }
}
