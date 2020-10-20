package com.atomikos.spring;

import java.util.Properties;

import javax.jms.Message;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.XADataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration;
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
@EnableConfigurationProperties(AtomikosProperties.class)
@ConditionalOnClass({ JtaTransactionManager.class, UserTransactionManager.class })
@ConditionalOnMissingBean(org.springframework.transaction.TransactionManager.class)
@AutoConfigureBefore(
        value = { XADataSourceAutoConfiguration.class, ActiveMQAutoConfiguration.class, ArtemisAutoConfiguration.class, HibernateJpaAutoConfiguration.class },
        name = "org.springframework.boot.autoconfigure.transaction.jta.JtaAutoConfiguration")
class AtomikosAutoConfiguration {

    @Bean(initMethod = "init", destroyMethod = "shutdownWait")
    @ConditionalOnMissingBean(UserTransactionService.class)
    UserTransactionServiceImp userTransactionService(AtomikosProperties atomikosProperties) {
        Properties properties = new Properties();
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
