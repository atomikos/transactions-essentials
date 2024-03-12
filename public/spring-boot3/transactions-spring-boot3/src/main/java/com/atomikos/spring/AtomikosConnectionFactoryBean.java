/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
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
 * Spring friendly version of {@link com.atomikos.jms.AtomikosConnectionFactoryBean}.
 */
@SuppressWarnings("serial")
@ConfigurationProperties(prefix = "spring.jta.atomikos.connectionfactory")
public class AtomikosConnectionFactoryBean extends com.atomikos.jms.AtomikosConnectionFactoryBean
        implements BeanNameAware, InitializingBean, DisposableBean {

    private String beanName;

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
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
