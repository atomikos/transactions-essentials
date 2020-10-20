package com.atomikos.spring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.assertj.core.data.MapEntry;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

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
        this.properties.setDefaultMaxWaitTimeOnShutdown(20);
        this.properties.setLogBaseName("logBaseName");
        this.properties.setLogBaseDir("logBaseDir");
        this.properties.setCheckpointInterval(4);
        this.properties.getRecovery().setForgetOrphanedLogEntriesDelay(Duration.ofMillis(2000));
        this.properties.getRecovery().setDelay(Duration.ofMillis(3000));
        this.properties.getRecovery().setMaxRetries(10);
        this.properties.getRecovery().setRetryInterval(Duration.ofMillis(4000));
        assertThat(this.properties.asProperties().size()).isEqualTo(17);
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
    }

    @Test
    public void testDefaultProperties() {
        Properties defaultSettings = loadDefaultSettings();
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
        assertThat(properties).contains(defaultOf(defaultSettings, keys.toArray(new String[0])));
        assertThat(properties).contains(entry("com.atomikos.icatch.recovery_delay",
                defaultSettings.get("com.atomikos.icatch.default_jta_timeout")));
        assertThat(properties).hasSize(15);
    }

    private MapEntry<?, ?>[] defaultOf(Properties defaultSettings, String... keys) {
        MapEntry<?, ?>[] entries = new MapEntry<?, ?>[keys.length];
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            entries[i] = entry(key, defaultSettings.get(key));
        }
        return entries;
    }

    private Properties loadDefaultSettings() {
        try {
            return PropertiesLoaderUtils.loadProperties(new ClassPathResource("transactions-defaults.properties"));
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to get default from Atomikos", ex);
        }
    }

    private void assertProperty(String key, String value) {
        assertThat(this.properties.asProperties().getProperty(key)).isEqualTo(value);
    }

}
