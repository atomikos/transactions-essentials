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
