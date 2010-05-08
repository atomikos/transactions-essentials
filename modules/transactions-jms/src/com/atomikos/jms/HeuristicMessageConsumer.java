//$Id: HeuristicMessageConsumer.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//$Log: HeuristicMessageConsumer.java,v $
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
//Revision 1.2  2006/03/15 10:32:04  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:15  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.4  2004/10/13 14:16:16  guy
//Updated javadoc and added String-based methods for heuristic receiver/sender.
//
//Revision 1.3  2004/10/12 13:04:36  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.2  2004/10/11 13:40:06  guy
//Fixed javadoc and EOL delimiters.
//
//$Id: HeuristicMessageConsumer.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//Revision 1.1.1.1  2004/09/18 12:42:50  guy
//$Id: HeuristicMessageConsumer.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//Added separate JMS module.
//$Id: HeuristicMessageConsumer.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//
//$Id: HeuristicMessageConsumer.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//Revision 1.3  2004/03/22 15:39:38  guy
//$Id: HeuristicMessageConsumer.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//Merged-in changes from branch redesign-4-2003.
//$Id: HeuristicMessageConsumer.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//
//$Id: HeuristicMessageConsumer.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//Revision 1.2.2.1  2003/06/20 16:32:10  guy
//$Id: HeuristicMessageConsumer.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//*** empty log message ***
//$Id: HeuristicMessageConsumer.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//
//$Id: HeuristicMessageConsumer.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//Revision 1.2  2003/03/11 06:43:00  guy
//$Id: HeuristicMessageConsumer.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//Merged in changes from transactionsJTA100 branch.
//$Id: HeuristicMessageConsumer.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//
//Revision 1.1.2.1  2002/09/12 14:23:57  guy
//Added JMS wrapper classes to make JMS transactional in a transparant way.
//

package com.atomikos.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;

import com.atomikos.icatch.HeuristicMessage;

/**
 * 
 * 
 * A message consumer with support for heuristic information. This information
 * is kept in the logs and can help to provide details in case of indoubt or
 * heuristic XA transactions during receive.
 */

public interface HeuristicMessageConsumer extends MessageConsumer
{


    /**
     * Block until a message is there, and use the supplied heuristic
     * information.
     * 
     * @param hmsg
     *            The heuristic information to show in case of problems.
     * @return Message The JMS message.
     * @throws JMSException
     */
    public Message receive ( String hmsg ) throws JMSException;



    /**
     * Block until a message is there, but use the supplied heuristic
     * information.
     * 
     * @param hmsg
     *            The heuristic information to show in case of problems.
     * @param timeout
     *            The timeout for receive.
     * @return Message The message or null on timeout.
     * @exception JMSException
     *                On error.
     */

    public Message receive ( long timeout ,  String hmsg ) throws JMSException;


    /**
     * Do not block until a message is there, and use the supplied heuristic
     * information.
     * 
     * @param hmsg
     *            The heuristic information to show in case of problems.
     * @return Message The message, or null if none.
     * @exception JMSException
     *                On error.
     */

    public Message receiveNoWait ( String hmsg ) throws JMSException;

}
