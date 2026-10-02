/**
 * Copyright (C) 2000-2026 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.spring;

import jakarta.transaction.UserTransaction;

import org.junit.Test;
import org.junit.Assert;
import org.springframework.boot.transaction.autoconfigure.TransactionProperties;
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

    private static final String[] AUTO_CONFIGURE_BEFORE = new String[] {
            "org.springframework.boot.transaction.jta.autoconfigure.JtaAutoConfiguration",
            "org.springframework.boot.jdbc.autoconfigure.XADataSourceAutoConfiguration"
    };

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

    @Test
    public void autoConfigureBeforeTargetsArePresent() {
        ClassLoader classLoader = AtomikosAutoConfigurationJUnit.class.getClassLoader();
        for (String fqcn : AUTO_CONFIGURE_BEFORE) {
            try {
                Class.forName(fqcn, false, classLoader);
            } catch (ClassNotFoundException e) {
                Assert.fail("Auto-configuration class not found: " + fqcn);
            }
        }
    }

}
