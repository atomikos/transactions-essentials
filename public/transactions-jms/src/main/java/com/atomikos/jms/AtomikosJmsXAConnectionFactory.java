/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jms;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.XAConnection;
import javax.jms.XAConnectionFactory;

import com.atomikos.datasource.pool.ConnectionFactory;
import com.atomikos.datasource.pool.CreateConnectionException;
import com.atomikos.datasource.pool.XPooledConnection;
import com.atomikos.datasource.xa.jms.JmsTransactionalResource;

class AtomikosJmsXAConnectionFactory implements ConnectionFactory<Connection>
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

	public XPooledConnection<Connection> createPooledConnection() throws CreateConnectionException
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
