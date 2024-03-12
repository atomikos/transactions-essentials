/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.spring;

import java.util.Arrays;
import java.util.HashSet;

import jakarta.jms.ConnectionFactory;
import javax.sql.DataSource;

import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.jms.extra.MessageDrivenContainer;
import org.junit.Test;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link AtomikosDependsOnBeanFactoryPostProcessor}.
 */
public class AtomikosDependsOnBeanFactoryPostProcessorTestJUnit {

    private AnnotationConfigApplicationContext context;

    @Test
    public void setsDependsOn() {
        this.context = new AnnotationConfigApplicationContext(Config.class);
        assertDependsOn("dataSource");
        assertDependsOn("connectionFactory");
        assertDependsOn("userTransactionManager", "dataSource", "connectionFactory");
        assertDependsOn("messageDrivenContainer", "userTransactionManager");
        this.context.close();
    }

    private void assertDependsOn(String bean, String... expected) {
        BeanDefinition definition = this.context.getBeanDefinition(bean);
        if (definition.getDependsOn() == null) {
            assertThat(expected).as("No dependsOn expected for " + bean).isEmpty();
            return;
        }
        HashSet<String> dependsOn = new HashSet<>(Arrays.asList(definition.getDependsOn()));
        assertThat(dependsOn).isEqualTo(new HashSet<>(Arrays.asList(expected)));
    }

    @Configuration(proxyBeanMethods = false)
    static class Config {

        @Bean
        DataSource dataSource() {
            return mock(DataSource.class);
        }

        @Bean
        ConnectionFactory connectionFactory() {
            return mock(ConnectionFactory.class);
        }

        @Bean
        UserTransactionManager userTransactionManager() {
            return mock(UserTransactionManager.class);
        }

        @Bean
        MessageDrivenContainer messageDrivenContainer() {
            return mock(MessageDrivenContainer.class);
        }

        @Bean
        static AtomikosDependsOnBeanFactoryPostProcessor atomikosPostProcessor() {
            return new AtomikosDependsOnBeanFactoryPostProcessor();
        }

    }

}
