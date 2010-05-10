//$Id: StringHeuristicMessage.java,v 1.1.1.1 2006/08/29 10:01:07 guy Exp $
//$Log: StringHeuristicMessage.java,v $
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
//Revision 1.1.1.1  2006/03/09 14:59:22  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.4  2005/08/09 15:23:39  guy
//Updated javadoc, and redesigned CompositeTransaction interface
//(eliminated TransactionControl and CompositeTerminator).
//
//Revision 1.3  2004/10/11 13:39:29  guy
//Fixed javadoc and EOL delimiters.
//
//$Id: StringHeuristicMessage.java,v 1.1.1.1 2006/08/29 10:01:07 guy Exp $
//Revision 1.2  2004/03/22 15:36:53  guy
//$Id: StringHeuristicMessage.java,v 1.1.1.1 2006/08/29 10:01:07 guy Exp $
//Merged-in changes from branch redesign-4-2003.
//$Id: StringHeuristicMessage.java,v 1.1.1.1 2006/08/29 10:01:07 guy Exp $
//
//$Id: StringHeuristicMessage.java,v 1.1.1.1 2006/08/29 10:01:07 guy Exp $
//Revision 1.1.2.1  2003/06/20 16:31:32  guy
//$Id: StringHeuristicMessage.java,v 1.1.1.1 2006/08/29 10:01:07 guy Exp $
//*** empty log message ***
//$Id: StringHeuristicMessage.java,v 1.1.1.1 2006/08/29 10:01:07 guy Exp $
//
//$Id: StringHeuristicMessage.java,v 1.1.1.1 2006/08/29 10:01:07 guy Exp $
//Revision 1.3  2003/03/11 06:43:25  guy
//$Id: StringHeuristicMessage.java,v 1.1.1.1 2006/08/29 10:01:07 guy Exp $
//Merged in changes from transactionsJTA100 branch.
//$Id: StringHeuristicMessage.java,v 1.1.1.1 2006/08/29 10:01:07 guy Exp $
//
//Revision 1.2.4.1  2002/08/30 15:08:53  guy
//Included serialVersionUID to have backward log compatibility.
//
//Revision 1.2  2002/01/29 11:26:28  guy
//Updated to latest state: repository seemed to be outdated?
//
//Revision 1.1  2001/03/01 19:27:17  pardon
//Added heur msg implementation.
//


package com.atomikos.icatch;


/**
 *
 *
 *A heuristic message implementation.
 */

public class StringHeuristicMessage implements HeuristicMessage
{
    //force set UID for backward log compatibility
    static final long serialVersionUID = -6967918138714056401L;
    
    protected String string_=null;
    
    /**
     *Constructor.
     *
     *@param string The message as a string.
     */

    public StringHeuristicMessage(String string)
    {
        string_=string;
    }

    /**
     *@see com.atomikos.icatch.HeuristicMessage
     */

    public String toString()
    {
        return string_;
    }
}
