/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.spring;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.atomikos.icatch.jta.UserTransactionManager;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.util.StringUtils;

/**
 * {@link BeanFactoryPostProcessor} to automatically setup the recommended
 * {@link BeanDefinition#setDependsOn(String[]) dependsOn} settings for
 * <a href="https://www.atomikos.com/Documentation/SpringIntegration">correct Atomikos
 * ordering</a>.
 */
public class AtomikosDependsOnBeanFactoryPostProcessor implements BeanFactoryPostProcessor, Ordered {

    private static final String[] NO_BEANS = {};

    private int order = Ordered.LOWEST_PRECEDENCE;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        String[] transactionManagers = beanFactory.getBeanNamesForType(UserTransactionManager.class, true, false);
        for (String transactionManager : transactionManagers) {
            addTransactionManagerDependencies(beanFactory, transactionManager);
        }
        addMessageDrivenContainerDependencies(beanFactory, transactionManagers);
    }

    private void addTransactionManagerDependencies(ConfigurableListableBeanFactory beanFactory,
            String transactionManager) {
        BeanDefinition bean = beanFactory.getBeanDefinition(transactionManager);
        Set<String> dependsOn = new LinkedHashSet<>(asList(bean.getDependsOn()));
        int initialSize = dependsOn.size();
        addDependencies(beanFactory, "jakarta.jms.ConnectionFactory", dependsOn);
        addDependencies(beanFactory, "javax.sql.DataSource", dependsOn);
        if (dependsOn.size() != initialSize) {
            bean.setDependsOn(StringUtils.toStringArray(dependsOn));
        }
    }

    private void addMessageDrivenContainerDependencies(ConfigurableListableBeanFactory beanFactory,
            String[] transactionManagers) {
        String[] messageDrivenContainers = getBeanNamesForType(beanFactory,
                "com.atomikos.jms.extra.MessageDrivenContainer");
        for (String messageDrivenContainer : messageDrivenContainers) {
            BeanDefinition bean = beanFactory.getBeanDefinition(messageDrivenContainer);
            Set<String> dependsOn = new LinkedHashSet<>(asList(bean.getDependsOn()));
            dependsOn.addAll(asList(transactionManagers));
            bean.setDependsOn(StringUtils.toStringArray(dependsOn));
        }
    }

    private void addDependencies(ConfigurableListableBeanFactory beanFactory, String type, Set<String> dependsOn) {
        dependsOn.addAll(asList(getBeanNamesForType(beanFactory, type)));
    }

    private String[] getBeanNamesForType(ConfigurableListableBeanFactory beanFactory, String type) {
        try {
            return beanFactory.getBeanNamesForType(Class.forName(type), true, false);
        }
        catch (ClassNotFoundException | NoClassDefFoundError ex) {
            // Ignore
        }
        return NO_BEANS;
    }

    private List<String> asList(String[] array) {
        return (array != null) ? Arrays.asList(array) : Collections.emptyList();
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

}
