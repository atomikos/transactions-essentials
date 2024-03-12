/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.spring.integrationtest;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.util.StringUtils;

/**
 * Basic integration tests for demo application.
 */
@ExtendWith(OutputCaptureExtension.class)
class SampleAtomikosApplicationTests {

    @Test
    void testTransactionRollback(CapturedOutput output) throws Exception {
        SampleAtomikosApplication.main();
        assertThat(output).satisfies(numberOfOccurrences("---->", 1));
        assertThat(output).satisfies(numberOfOccurrences("----> josh", 1));
        assertThat(output).satisfies(numberOfOccurrences("Count is 1", 2));
        assertThat(output).satisfies(numberOfOccurrences("Simulated error", 1));
    }

    @Test
    void testTransactionRollbackWithoutSpringBoot23JtaAutoConfiguration(CapturedOutput output) throws Exception {
        SampleAtomikosApplication.main("--spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.transaction.jta.JtaAutoConfiguration");
        assertThat(output).satisfies(numberOfOccurrences("---->", 2));
        assertThat(output).satisfies(numberOfOccurrences("----> josh", 1));
        assertThat(output).satisfies(numberOfOccurrences("Count is 1", 2));
        assertThat(output).satisfies(numberOfOccurrences("Simulated error", 1));
    }

    private <T extends CharSequence> Consumer<T> numberOfOccurrences(String substring, int expectedCount) {
        return (charSequence) -> {
            int count = StringUtils.countOccurrencesOf(charSequence.toString(), substring);
            assertThat(count).isEqualTo(expectedCount);
        };
    }

}
