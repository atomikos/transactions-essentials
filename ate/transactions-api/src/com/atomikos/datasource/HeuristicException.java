//$Id: HeuristicException.java,v 1.1.1.1 2006/08/29 10:01:07 guy Exp $
//$Log: HeuristicException.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:07  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:40  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:34  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:29  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:57  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:21  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.3  2004/03/22 15:39:29  guy
//Merged-in changes from branch redesign-4-2003.
//
//Revision 1.2.10.1  2003/06/20 16:32:05  guy
//*** empty log message ***
//
//Revision 1.2  2001/11/30 13:29:47  guy
//Updated files: UniqueId changed to String.
//
//Revision 1.4  2001/02/25 11:13:40  pardon
//Added a lot.
//
//Revision 1.3  2001/02/21 10:08:00  pardon
//Added only needed files.
//
//Revision 1.1  2001/02/19 18:23:13  pardon
//Added new interfaces and classes for redesign.
//

package com.atomikos.datasource;
import com.atomikos.icatch.HeuristicMessage;

public class HeuristicException extends ResourceException
{
    protected HeuristicMessage[] myMsgs=null;

    

    /**
     *Constructor.
     *
     */
    public HeuristicException(String msg)
    {
        super(msg);
    }
    
    /**
     *Constructor.
     *@param msgs an array of heuristic messages,
     *or null if none.
     */
    public HeuristicException(HeuristicMessage[] msgs)
    {
        super("Heuristic Exception");
        myMsgs=msgs;
    }
    
    /**
     *Get any heuristic messages.
     *
     *@return HeuristicMessage[] A list of messages, or null if none.
     */
    public HeuristicMessage[] getHeuristicMessages(){
        return myMsgs;
    }
}
