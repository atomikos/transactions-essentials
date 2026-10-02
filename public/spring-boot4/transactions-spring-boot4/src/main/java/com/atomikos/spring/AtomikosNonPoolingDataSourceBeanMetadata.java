/**
 * Copyright (C) 2000-2026 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.spring;

import org.springframework.boot.jdbc.metadata.AbstractDataSourcePoolMetadata;

public class AtomikosNonPoolingDataSourceBeanMetadata extends AbstractDataSourcePoolMetadata<AtomikosNonPoolingDataSourceBean> {


	public AtomikosNonPoolingDataSourceBeanMetadata(AtomikosNonPoolingDataSourceBean dataSource) {
		super(dataSource);
	}

	@Override
	public Integer getActive() {
		return 0;
	}

	@Override
	public Integer getMax() {
		return 0;
	}

	@Override
	public Integer getMin() {
		return 0;
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
