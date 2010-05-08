//$Id: JtaQueueReceiver.java,v 1.3 2006/10/30 10:37:09 guy Exp $
//$Log: JtaQueueReceiver.java,v $
//Revision 1.3  2006/10/30 10:37:09  guy
//Merged in changes of 3.1.0 release
//
//Revision 1.2.2.1  2006/10/13 13:07:04  guy
//ADDED 1010
//
//Revision 1.2  2006/09/22 09:27:17  guy
//Removed ref to old name: TransactionsJTA
//
//Revision 1.1.1.1  2006/08/29 10:01:13  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:38  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:32  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:28  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:54  guy
//Import.
//
//Revision 1.3  2006/03/21 13:23:20  guy
//Introduced active recovery and CompTx properties as meta-tags.
//
//Revision 1.2  2006/03/15 10:32:05  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:15  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.4  2005/05/10 08:45:00  guy
//Merged-in changes of Transactions_2_03 branch.
//
//Revision 1.3.2.1  2005/02/07 13:53:00  guy
//Added description in exceptions.
//
//Revision 1.3  2004/10/13 14:16:16  guy
//Updated javadoc and added String-based methods for heuristic receiver/sender.
//
//Revision 1.2  2004/10/12 13:04:37  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//$Id: JtaQueueReceiver.java,v 1.3 2006/10/30 10:37:09 guy Exp $
//Revision 1.1.1.1  2004/09/18 12:42:50  guy
//$Id: JtaQueueReceiver.java,v 1.3 2006/10/30 10:37:09 guy Exp $
//Added separate JMS module.
//$Id: JtaQueueReceiver.java,v 1.3 2006/10/30 10:37:09 guy Exp $
//
//$Id: JtaQueueReceiver.java,v 1.3 2006/10/30 10:37:09 guy Exp $
//Revision 1.3  2004/03/22 15:39:38  guy
//$Id: JtaQueueReceiver.java,v 1.3 2006/10/30 10:37:09 guy Exp $
//Merged-in changes from branch redesign-4-2003.
//$Id: JtaQueueReceiver.java,v 1.3 2006/10/30 10:37:09 guy Exp $
//
//$Id: JtaQueueReceiver.java,v 1.3 2006/10/30 10:37:09 guy Exp $
//Revision 1.2.2.1  2003/06/20 16:32:10  guy
//$Id: JtaQueueReceiver.java,v 1.3 2006/10/30 10:37:09 guy Exp $
//*** empty log message ***
//$Id: JtaQueueReceiver.java,v 1.3 2006/10/30 10:37:09 guy Exp $
//
//$Id: JtaQueueReceiver.java,v 1.3 2006/10/30 10:37:09 guy Exp $
//Revision 1.2  2003/03/11 06:43:00  guy
//$Id: JtaQueueReceiver.java,v 1.3 2006/10/30 10:37:09 guy Exp $
//Merged in changes from transactionsJTA100 branch.
//$Id: JtaQueueReceiver.java,v 1.3 2006/10/30 10:37:09 guy Exp $
//
//Revision 1.1.2.2  2002/09/13 09:02:18  guy
//Corrected initial mistakes.
//
//Revision 1.1.2.1  2002/09/12 14:23:58  guy
//Added JMS wrapper classes to make JMS transactional in a transparant way.
//

package com.atomikos.jms;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueReceiver;
import javax.transaction.xa.XAResource;

import com.atomikos.datasource.TransactionalResource;

/**
 * 
 * 
 * A queue receiver with heuristic info.
 */

class JtaQueueReceiver 
extends DefaultJtaMessageConsumer
implements HeuristicQueueReceiver
{
  

    JtaQueueReceiver ( QueueReceiver receiver , TransactionalResource res ,
            XAResource xares )
    {
        super ( receiver , res , xares );
    }

    private QueueReceiver getReceiver()
    {
    		return ( QueueReceiver ) getMessageConsumer();
    }
    
    public Queue getQueue () throws JMSException
    {
        return getReceiver().getQueue ();
    }


}
