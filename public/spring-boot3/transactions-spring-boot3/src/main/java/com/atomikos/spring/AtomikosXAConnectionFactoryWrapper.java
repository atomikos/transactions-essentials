/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.spring;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.XAConnectionFactory;

import org.springframework.boot.jms.XAConnectionFactoryWrapper;

/**
 * {@link XAConnectionFactoryWrapper} that uses an {@link AtomikosConnectionFactoryBean}
 * to wrap a {@link XAConnectionFactory}.
 */
public class AtomikosXAConnectionFactoryWrapper implements XAConnectionFactoryWrapper {

    @Override
    public ConnectionFactory wrapConnectionFactory(XAConnectionFactory connectionFactory) {
        AtomikosConnectionFactoryBean bean = new AtomikosConnectionFactoryBean();
        bean.setXaConnectionFactory(connectionFactory);
        return bean;
    }

}
