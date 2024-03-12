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

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link AtomikosXAConnectionFactoryWrapper}.
 */
public class AtomikosXAConnectionFactoryWrapperTestJUnit {

    @Test
    public void wrap() {
        XAConnectionFactory connectionFactory = mock(XAConnectionFactory.class);
        AtomikosXAConnectionFactoryWrapper wrapper = new AtomikosXAConnectionFactoryWrapper();
        ConnectionFactory wrapped = wrapper.wrapConnectionFactory(connectionFactory);
        assertThat(wrapped).isInstanceOf(AtomikosConnectionFactoryBean.class);
        assertThat(((AtomikosConnectionFactoryBean) wrapped).getXaConnectionFactory()).isSameAs(connectionFactory);
    }

}
