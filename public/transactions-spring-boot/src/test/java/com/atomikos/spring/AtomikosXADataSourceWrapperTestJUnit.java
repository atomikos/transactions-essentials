package com.atomikos.spring;

import javax.sql.DataSource;
import javax.sql.XADataSource;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link AtomikosXADataSourceWrapper}.
 */
public class AtomikosXADataSourceWrapperTestJUnit {

    @Test
    public void wrap() throws Exception {
        XADataSource dataSource = mock(XADataSource.class);
        AtomikosXADataSourceWrapper wrapper = new AtomikosXADataSourceWrapper();
        DataSource wrapped = wrapper.wrapDataSource(dataSource);
        assertThat(wrapped).isInstanceOf(AtomikosDataSourceBean.class);
        assertThat(((AtomikosDataSourceBean) wrapped).getXaDataSource()).isSameAs(dataSource);
    }

}
