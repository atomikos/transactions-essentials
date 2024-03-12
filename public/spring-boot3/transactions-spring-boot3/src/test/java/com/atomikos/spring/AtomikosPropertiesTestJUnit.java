/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.spring;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Test;

/**
 * Tests for {@link AtomikosProperties}.
 */
public class AtomikosPropertiesTestJUnit {

    private AtomikosProperties properties = new AtomikosProperties();

    @Test
    public void testProperties() {
        this.properties.setService("service");
        this.properties.setMaxTimeout(Duration.ofMillis(1));
        this.properties.setDefaultJtaTimeout(Duration.ofMillis(2));
        this.properties.setMaxActives(3);
        this.properties.setEnableLogging(true);
        this.properties.setTransactionManagerUniqueName("uniqueName");
        this.properties.setSerialJtaTransactions(true);
        this.properties.setAllowSubTransactions(false);
        this.properties.setForceShutdownOnVmExit(true);
        this.properties.setDefaultMaxWaitTimeOnShutdown(20l);
        this.properties.setLogBaseName("logBaseName");
        this.properties.setLogBaseDir("logBaseDir");
        this.properties.setCheckpointInterval(4l);
        this.properties.getRecovery().setForgetOrphanedLogEntriesDelay(Duration.ofMillis(2000));
        this.properties.getRecovery().setDelay(Duration.ofMillis(3000));
        this.properties.getRecovery().setMaxRetries(10);
        this.properties.getRecovery().setRetryInterval(Duration.ofMillis(4000));
        this.properties.setThrowOnHeuristic(true);
        assertThat(this.properties.asProperties().size()).isEqualTo(18);
        assertProperty("com.atomikos.icatch.service", "service");
        assertProperty("com.atomikos.icatch.max_timeout", "1");
        assertProperty("com.atomikos.icatch.default_jta_timeout", "2");
        assertProperty("com.atomikos.icatch.max_actives", "3");
        assertProperty("com.atomikos.icatch.enable_logging", "true");
        assertProperty("com.atomikos.icatch.tm_unique_name", "uniqueName");
        assertProperty("com.atomikos.icatch.serial_jta_transactions", "true");
        assertProperty("com.atomikos.icatch.allow_subtransactions", "false");
        assertProperty("com.atomikos.icatch.force_shutdown_on_vm_exit", "true");
        assertProperty("com.atomikos.icatch.default_max_wait_time_on_shutdown", "20");
        assertProperty("com.atomikos.icatch.log_base_name", "logBaseName");
        assertProperty("com.atomikos.icatch.log_base_dir", "logBaseDir");
        assertProperty("com.atomikos.icatch.checkpoint_interval", "4");
        assertProperty("com.atomikos.icatch.forget_orphaned_log_entries_delay", "2000");
        assertProperty("com.atomikos.icatch.recovery_delay", "3000");
        assertProperty("com.atomikos.icatch.oltp_max_retries", "10");
        assertProperty("com.atomikos.icatch.oltp_retry_interval", "4000");
        assertProperty("com.atomikos.icatch.throw_on_heuristic", "true");
    }

    @Test
    public void testDefaultProperties() {
        Properties properties = this.properties.asProperties();
        List<String> keys = new ArrayList<>();
        keys.add("com.atomikos.icatch.max_timeout");
        keys.add("com.atomikos.icatch.default_jta_timeout");
        keys.add("com.atomikos.icatch.max_actives");
        keys.add("com.atomikos.icatch.enable_logging");
        keys.add("com.atomikos.icatch.serial_jta_transactions");
        keys.add("com.atomikos.icatch.allow_subtransactions");
        keys.add("com.atomikos.icatch.force_shutdown_on_vm_exit");
        keys.add("com.atomikos.icatch.default_max_wait_time_on_shutdown");
        keys.add("com.atomikos.icatch.log_base_name");
        keys.add("com.atomikos.icatch.checkpoint_interval");
        keys.add("com.atomikos.icatch.forget_orphaned_log_entries_delay");
        keys.add("com.atomikos.icatch.oltp_max_retries");
        keys.add("com.atomikos.icatch.oltp_retry_interval");
        keys.add("com.atomikos.icatch.throw_on_heuristic");
        assertThat(properties).isEmpty();
    }

    private void assertProperty(String key, String value) {
        assertThat(this.properties.asProperties().getProperty(key)).isEqualTo(value);
    }

}
