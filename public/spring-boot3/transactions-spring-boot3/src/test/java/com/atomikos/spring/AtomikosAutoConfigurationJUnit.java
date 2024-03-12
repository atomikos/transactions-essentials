/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.spring;

import jakarta.transaction.UserTransaction;

import org.junit.Test;
import org.springframework.boot.autoconfigure.transaction.TransactionProperties;
import org.springframework.boot.jdbc.XADataSourceWrapper;
import org.springframework.boot.jms.XAConnectionFactoryWrapper;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.transaction.jta.JtaTransactionManager;

import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.jta.UserTransactionManager;

/**
 * Tests for {@link AtomikosAutoConfiguration}.
 */
public class AtomikosAutoConfigurationJUnit {

    @Test
    public void sanityCheck() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TransactionProperties.class,
                AtomikosAutoConfiguration.class)) {
            context.getBean(AtomikosProperties.class);
            context.getBean(UserTransactionService.class);
            context.getBean(UserTransactionManager.class);
            context.getBean(UserTransaction.class);
            context.getBean(XADataSourceWrapper.class);
            context.getBean(XAConnectionFactoryWrapper.class);
            context.getBean(AtomikosDependsOnBeanFactoryPostProcessor.class);
            context.getBean(JtaTransactionManager.class);
        }
    }

}
