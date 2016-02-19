/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * This code ("Atomikos TransactionsEssentials"), by itself,
 * is being distributed under the
 * Apache License, Version 2.0 ("License"), a copy of which may be found at
 * http://www.atomikos.com/licenses/apache-license-2.0.txt .
 * You may not use this file except in compliance with the License.
 *
 * While the License grants certain patent license rights,
 * those patent license rights only extend to the use of
 * Atomikos TransactionsEssentials by itself.
 *
 * This code (Atomikos TransactionsEssentials) contains certain interfaces
 * in package (namespace) com.atomikos.icatch
 * (including com.atomikos.icatch.Participant) which, if implemented, may
 * infringe one or more patents held by Atomikos.
 * It should be appreciated that you may NOT implement such interfaces;
 * licensing to implement these interfaces must be obtained separately from Atomikos.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package com.atomikos.jndi.spring;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.atomikos.jdbc.nonxa.AtomikosNonXADataSourceBean;
import com.atomikos.jms.AtomikosConnectionFactoryBean;
import com.atomikos.jms.extra.MessageDrivenContainer;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

/**
 * Ensures proper init/shutdown ordering so recovery is streamlined.
 * 
 * Add an instance of this class to your Spring config to ensure proper startup and shutdown ordering of all Atomikos beans.
 * Make sure to remove all depends-on (as well as init and destroy method) declarations in your Spring config, 
 * because they will conflict with this bean's work!
 */

public class AtomikosApplicationContextAware  implements
		ApplicationContextAware, InitializingBean, DisposableBean {

	private static Logger LOG = LoggerFactory.createLogger(AtomikosApplicationContextAware.class); 

	private ApplicationContext applicationContext;

	public void afterPropertiesSet() throws Exception {
		initialize(AtomikosDataSourceBean.class,"init");
		initialize(AtomikosNonXADataSourceBean.class,"init");
		initialize(AtomikosConnectionFactoryBean.class,"init");
		initialize(UserTransactionManager.class,"init");
		initialize(MessageDrivenContainer.class,"start");
		logWarningFor(UserTransactionServiceImp.class);
	}

	private <T> void logWarningFor(Class<T> clazz) {
		Map<String, T> beansOfType = applicationContext.getBeansOfType(clazz);
		if (!beansOfType.isEmpty()) {			
			LOG.logWarning("Class " + clazz.getName() + " is not expected in Spring configuration - automatic init and shutdown ordering not supported");
		}
	}

	private <T> void initialize(Class<T> clazz, String initMethodName) throws Exception {
		Map<String, T> beansOfType = applicationContext.getBeansOfType(clazz);
		Collection<T> beansToInit = beansOfType.values();

		Method initMethod = clazz.getMethod(initMethodName, new Class[] {});

		for (T atomikosBean : beansToInit) {
			initMethod.invoke(atomikosBean, new Object[] {});
		}

	}

	public void destroy() throws Exception {
		doClose(MessageDrivenContainer.class,"stop");
		doClose(UserTransactionManager.class,"close");
		doClose(AtomikosDataSourceBean.class,"close");
		doClose(AtomikosNonXADataSourceBean.class,"close");
		doClose(AtomikosConnectionFactoryBean.class,"close");
		
	}

	private <T> void doClose(Class<T> clazz, String closeMethodName) throws Exception {
		Method closeMethod = clazz.getMethod(closeMethodName, new Class[] {});
		Map<String, T> beansOfType = applicationContext.getBeansOfType(clazz);
		Collection<T> beansToClose = beansOfType.values();
		for (T beanToClose : beansToClose) {
			closeMethod.invoke(beanToClose, new Object[] {});
		}
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
