/**
 * Copyright (C) 2000-2026 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.spring;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

/**
 * Spring friendly version of {@link com.atomikos.jdbc.AtomikosDataSourceBean}.
 */
@SuppressWarnings("serial")
@ConfigurationProperties(prefix = "spring.jta.atomikos.datasource")
public class AtomikosDataSourceBean extends com.atomikos.jdbc.AtomikosDataSourceBean
		implements BeanNameAware, InitializingBean, DisposableBean {

	private String beanName;
	
	/**
	 * The value of this property is used by the configuration to determine what
	 * datasource to instantiate. It is not passed on to the actual underlying
	 * datasource because there it is not needed by the Atomikos codebase.
	 */
	private boolean enablePool = true;
	
	@Override
	public void setBeanName(String name) {
		this.beanName = name;
	}

	public void setEnablePool(boolean enablePool) {
		this.enablePool = enablePool;
	}
	
	public boolean isEnablePool() {
		return enablePool;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (!StringUtils.hasLength(getUniqueResourceName())) {
			setUniqueResourceName(this.beanName);
		}
		init();
	}

	@Override
	public void destroy() throws Exception {
		close();
	}



}
