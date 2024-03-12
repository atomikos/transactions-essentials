/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.spring;

import java.util.Properties;

import jakarta.jms.Message;
import jakarta.transaction.TransactionManager;
import jakarta.transaction.UserTransaction;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.XADataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.artemis.ArtemisAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.XADataSourceWrapper;
import org.springframework.boot.jms.XAConnectionFactoryWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.jta.JtaTransactionManager;

import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.atomikos.icatch.jta.UserTransactionManager;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for Atomikos JTA.
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({ SpringJtaAtomikosProperties.class, AtomikosProperties.class})
@ConditionalOnClass({ JtaTransactionManager.class, UserTransactionManager.class })
@ConditionalOnMissingBean(org.springframework.transaction.TransactionManager.class)
@AutoConfigureBefore(
        value = { XADataSourceAutoConfiguration.class, ArtemisAutoConfiguration.class, HibernateJpaAutoConfiguration.class },
        name = "org.springframework.boot.autoconfigure.transaction.jta.JtaAutoConfiguration")
public class AtomikosAutoConfiguration {

    @Bean(initMethod = "init", destroyMethod = "shutdownWait")
    @ConditionalOnMissingBean(UserTransactionService.class)
    UserTransactionServiceImp userTransactionService(SpringJtaAtomikosProperties springJtaAtomikosProperties, AtomikosProperties atomikosProperties) {
        Properties properties = new Properties();
        properties.putAll(springJtaAtomikosProperties.asProperties());
        properties.putAll(atomikosProperties.asProperties());
        return new UserTransactionServiceImp(properties);
    }


    @Bean(initMethod = "init", destroyMethod = "close")
    @ConditionalOnMissingBean(TransactionManager.class)
    UserTransactionManager atomikosTransactionManager(UserTransactionService userTransactionService) throws Exception {
        UserTransactionManager manager = new UserTransactionManager();
        manager.setStartupTransactionService(false);
        manager.setForceShutdown(true);
        return manager;
    }

    @Bean
    @ConditionalOnMissingBean(XADataSourceWrapper.class)
    AtomikosXADataSourceWrapper xaDataSourceWrapper() {
        return new AtomikosXADataSourceWrapper();
    }

    @Bean
    @ConditionalOnMissingBean
    static AtomikosDependsOnBeanFactoryPostProcessor atomikosDependsOnBeanFactoryPostProcessor() {
        return new AtomikosDependsOnBeanFactoryPostProcessor();
    }

    @Bean
    JtaTransactionManager transactionManager(UserTransaction userTransaction, TransactionManager transactionManager,
            ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers) {
        JtaTransactionManager jtaTransactionManager = new JtaTransactionManager(userTransaction, transactionManager);
        transactionManagerCustomizers.ifAvailable((customizers) -> customizers.customize(jtaTransactionManager));
        return jtaTransactionManager;
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(Message.class)
    static class AtomikosJtaJmsConfiguration {

        @Bean
        @ConditionalOnMissingBean(XAConnectionFactoryWrapper.class)
        AtomikosXAConnectionFactoryWrapper xaConnectionFactoryWrapper() {
            return new AtomikosXAConnectionFactoryWrapper();
        }

    }

}
