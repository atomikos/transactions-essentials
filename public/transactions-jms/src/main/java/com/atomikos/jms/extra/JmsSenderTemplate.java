/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jms.extra;

import java.io.Serializable;
import java.util.Map;

import javax.jms.JMSException;

/**
 *  Common interface for JMS send functionality so client can benefit from dependency injection. 
 */
public interface JmsSenderTemplate {

	/**
	 * Executes an application-level call-back within the managed session.
	 *
	 * @param callback
	 * @throws JMSException
	 */
	void executeCallback(JmsSenderTemplateCallback callback) throws JMSException;

	/**
	 * Sends a TextMessage.
	 *
	 * @param content The text as a string.
	 * @throws JMSException
	 */
	void sendTextMessage(String content) throws JMSException;

	/**
	 * Sends a MapMessage.
	 *
	 * @param content The Map to get the content from.
	 *
	 * @throws JMSException
	 */
	
	void sendMapMessage(Map<String,?> content) throws JMSException;

	/**
	 * Sends an ObjectMessage.
	 *
	 * @param content The serializable object content.
	 * @throws JMSException
	 */
	void sendObjectMessage(Serializable content)
			throws JMSException;

	/**
	 * Sends a ByteMessage.
	 *
	 * @param content The content as a byte array.
	 * @throws JMSException
	 */
	void sendBytesMessage(byte[] content) throws JMSException;

}