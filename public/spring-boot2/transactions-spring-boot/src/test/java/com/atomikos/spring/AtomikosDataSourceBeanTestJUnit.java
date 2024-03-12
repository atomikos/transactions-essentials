/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.spring;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link AtomikosDataSourceBean}.
 */
public class AtomikosDataSourceBeanTestJUnit {

    @Test
    public void beanMethods() throws Exception {
        MockAtomikosDataSourceBean bean = spy(new MockAtomikosDataSourceBean());
        bean.setBeanName("bean");
        bean.afterPropertiesSet();
        assertThat(bean.getUniqueResourceName()).isEqualTo("bean");
        verify(bean).init();
        verify(bean, never()).close();
        bean.destroy();
        verify(bean).close();
    }

    @SuppressWarnings("serial")
    static class MockAtomikosDataSourceBean extends AtomikosDataSourceBean {

        @Override
        public synchronized void init() {
        }

        @Override
        public void close() {
        }

    }

}
