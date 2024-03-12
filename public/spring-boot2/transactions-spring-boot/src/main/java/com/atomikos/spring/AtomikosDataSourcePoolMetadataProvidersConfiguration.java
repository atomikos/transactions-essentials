/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.spring;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.jdbc.DataSourceUnwrapper;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadataProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.atomikos.jdbc.AtomikosNonXADataSourceBean;

/**
 * Register the {@link AtomikosDataSourcePoolMetadataProvidersConfiguration} for the AtomikosDataSourceBeans. For use in the Spring Boot actuator:
 * https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-features.html#production-ready-metrics-jdbc. 
 * This configuration must be explicitly imported:
 * <p>
 * {@code @Import({ AtomikosDataSourcePoolMetadataProvidersConfiguration.class}) }
 * </p>
 */
@Configuration
public class AtomikosDataSourcePoolMetadataProvidersConfiguration {

	@Configuration
	@ConditionalOnClass(AtomikosDataSourceBean.class)
	static class AtomikosDataSourceBeanMetadataProviderConfiguration {

		@Bean
		public DataSourcePoolMetadataProvider atomikosDataSourceBeanMetadataProvider() {

			return (dataSource) -> {
				AtomikosDataSourceBean atomikosDataSource = DataSourceUnwrapper.unwrap(dataSource, AtomikosDataSourceBean.class);
				if (atomikosDataSource != null) {
					return new AtomikosDataSourceBeanMetadata(atomikosDataSource);
				}
				return null;
			};
		}
	}

	@Configuration
	@ConditionalOnClass(AtomikosNonXADataSourceBean.class)
	static class AtomikosNonXADataSourceBeanMetadataProviderConfiguration {

		@Bean
		public DataSourcePoolMetadataProvider atomikosNonXADataSourceBeanMetadataProvider() {
			
			return (dataSource) -> {
				AtomikosNonXADataSourceBean atomikosDataSource = DataSourceUnwrapper.unwrap(dataSource, AtomikosNonXADataSourceBean.class);
				if (atomikosDataSource != null) {
					return new AtomikosNonXADataSourceBeanMetadata(atomikosDataSource);
				}
				return null;
			};
		}
	}
}
