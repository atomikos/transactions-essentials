//$Id: HeuristicMessage.java,v 1.1.1.1 2006/08/29 10:01:07 guy Exp $
//$Log: HeuristicMessage.java,v $
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
//Revision 1.5  2005/08/09 15:23:38  guy
//Updated javadoc, and redesigned CompositeTransaction interface
//(eliminated TransactionControl and CompositeTerminator).
//
//Revision 1.4  2004/10/25 08:45:56  guy
//Updated TODOs
//
//Revision 1.3  2004/10/12 13:03:26  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.2  2004/03/22 15:36:53  guy
//Merged-in changes from branch redesign-4-2003.
//
//Revision 1.1.2.1  2003/06/20 16:31:32  guy
//*** empty log message ***
//
//Revision 1.2.10.1  2003/05/22 14:25:20  guy
//*** empty log message ***
//
//Revision 1.2  2002/01/29 11:26:28  guy
//Updated to latest state: repository seemed to be outdated?
//


package com.atomikos.icatch;
import java.io.Serializable;
/**
 *
 *
 *A message to help resolving heuristic problem cases.
 *Instances can be given to the resource, which will keep
 *them and return them as part of a heuristic exception.
 *
 */

public interface HeuristicMessage extends Serializable
{
    /**
     *Get the description of the heuristically terminated
     *work.
     *@return String the description in string format.
     */
    public String toString();

}
