/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jms;

import javax.jms.JMSException;
import javax.jms.XAConnection;
import javax.jms.XAConnectionFactory;

import com.atomikos.datasource.pool.ConnectionFactory;
import com.atomikos.datasource.pool.CreateConnectionException;
import com.atomikos.datasource.pool.XPooledConnection;
import com.atomikos.datasource.xa.jms.JmsTransactionalResource;

class AtomikosJmsXAConnectionFactory implements ConnectionFactory
{

	private XAConnectionFactory xaConnectionFactory;
	private JmsTransactionalResource jmsTransactionalResource;
	private AtomikosConnectionFactoryBean atomikosConnectionFactory;


	public AtomikosJmsXAConnectionFactory(XAConnectionFactory xaConnectionFactory,
			JmsTransactionalResource jmsTransactionalResource,
			AtomikosConnectionFactoryBean atomikosConnectionFactory)
	{
		this.xaConnectionFactory = xaConnectionFactory;
		this.jmsTransactionalResource = jmsTransactionalResource;
		this.atomikosConnectionFactory = atomikosConnectionFactory;
	}

	public XPooledConnection createPooledConnection() throws CreateConnectionException
	{
		XAConnection xac;
		try {
			xac = xaConnectionFactory.createXAConnection();
			return new AtomikosPooledJmsConnection(atomikosConnectionFactory.getIgnoreSessionTransactedFlag(), xac, jmsTransactionalResource, atomikosConnectionFactory);
		} catch (JMSException ex) {
			throw new CreateConnectionException("error creating JMS connection", ex);
		}
	}

}
