/**
 * Copyright (C) 2000-2026 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.spring;

import javax.sql.XADataSource;

import org.springframework.boot.jdbc.XADataSourceWrapper;

/**
 * {@link XADataSourceWrapper} that uses an {@link AtomikosNonPoolingDataSourceBean} to wrap a
 * {@link XADataSource}.
 */
public class AtomikosNonPoolingXADataSourceWrapper implements XADataSourceWrapper {

    @Override
    public AtomikosNonPoolingDataSourceBean wrapDataSource(XADataSource dataSource) throws Exception {
    	AtomikosNonPoolingDataSourceBean bean = new AtomikosNonPoolingDataSourceBean();
        bean.setXaDataSource(dataSource);
        return bean;
    }

}
