package com.atomikos.jms;

import javax.jms.TopicSubscriber;

/**
 * 
 * 
 * A topic subscriber with support for heuristic information. This information is
 * kept in the logs and can help to provide details in case of indoubt or
 * heuristic XA transactions during receive. All topic subscribers that you create
 * via the Atomikos JMS classes will return an instance of
 * this type. You can access the functionality by type-casting.
 * <p>
 * Topic functionality in this product was sponsored by <a href="http://www.webtide.com">Webtide</a>.
 */
public interface HeuristicTopicSubscriber 
extends HeuristicMessageConsumer, TopicSubscriber
{

}
