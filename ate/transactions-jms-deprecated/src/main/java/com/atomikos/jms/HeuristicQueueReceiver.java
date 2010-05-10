//$Id: HeuristicQueueReceiver.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//$Log: HeuristicQueueReceiver.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:12  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:38  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:31  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:28  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:54  guy
//Import.
//
//Revision 1.2  2006/03/15 10:32:05  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:15  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.4  2005/08/09 15:25:21  guy
//Updated javadoc.
//
//Revision 1.3  2004/10/13 14:16:16  guy
//Updated javadoc and added String-based methods for heuristic receiver/sender.
//
//Revision 1.2  2004/10/11 13:40:06  guy
//Fixed javadoc and EOL delimiters.
//
//$Id: HeuristicQueueReceiver.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//Revision 1.1.1.1  2004/09/18 12:42:50  guy
//$Id: HeuristicQueueReceiver.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//Added separate JMS module.
//$Id: HeuristicQueueReceiver.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//
//$Id: HeuristicQueueReceiver.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//Revision 1.2  2003/03/11 06:43:00  guy
//$Id: HeuristicQueueReceiver.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//Merged in changes from transactionsJTA100 branch.
//$Id: HeuristicQueueReceiver.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//
//Revision 1.1.2.1  2002/09/12 14:23:57  guy
//Added JMS wrapper classes to make JMS transactional in a transparant way.
//

package com.atomikos.jms;

import javax.jms.QueueReceiver;

/**
 * 
 * 
 * A queue receiver with support for heuristic information. This information is
 * kept in the logs and can help to provide details in case of indoubt or
 * heuristic XA transactions during receive. All queue receivers that you create
 * via the Atomikos QueueConnectionFactory classes will return a receiver of
 * this type. You can access the functionality by type-casting.
 */

public interface HeuristicQueueReceiver extends HeuristicMessageConsumer,
        QueueReceiver
{

}
