package com.atomikos.spring;

import javax.sql.XADataSource;

import org.springframework.boot.jdbc.XADataSourceWrapper;

/**
 * {@link XADataSourceWrapper} that uses an {@link AtomikosDataSourceBean} to wrap a
 * {@link XADataSource}.
 */
public class AtomikosXADataSourceWrapper implements XADataSourceWrapper {

    @Override
    public AtomikosDataSourceBean wrapDataSource(XADataSource dataSource) throws Exception {
        AtomikosDataSourceBean bean = new AtomikosDataSourceBean();
        bean.setXaDataSource(dataSource);
        return bean;
    }

}
