/**
 * Copyright (C) 2000-2023 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.spring;

import org.springframework.boot.jdbc.metadata.AbstractDataSourcePoolMetadata;

import com.atomikos.jdbc.AtomikosDataSourceBean;

public class AtomikosDataSourceBeanMetadata extends AbstractDataSourcePoolMetadata<AtomikosDataSourceBean> {

	
	public AtomikosDataSourceBeanMetadata(AtomikosDataSourceBean dataSource) {
		super(dataSource);
	}
	

	@Override
	public Integer getActive() {
		return getDataSource().poolTotalSize() - getDataSource().poolAvailableSize();
	}

	@Override
	public Integer getMax() {
		return getDataSource().getMaxPoolSize();
	}

	@Override
	public Integer getMin() {
		return getDataSource().getMinPoolSize();
	}

	@Override
	public String getValidationQuery() {
		return getDataSource().getTestQuery();
	}

	@Override
	public Boolean getDefaultAutoCommit() {
		return false;
	}

}
