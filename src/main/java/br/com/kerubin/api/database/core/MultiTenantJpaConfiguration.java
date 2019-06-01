package br.com.kerubin.api.database.core;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.hibernate.MultiTenancyStrategy;
import org.hibernate.cfg.Environment;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import br.com.kerubin.api.database.entity.AuditorAwareImpl;

@Configuration
@EnableJpaRepositories(basePackages = "br.com.kerubin.api", transactionManagerRef = "tranManager")
@EnableTransactionManagement
@EnableJpaAuditing(auditorAwareRef="auditorAware")
//includeFilters = @Filter(type = FilterType.REGEX, pattern="br.com.kerubin.api.kerubin.clientes.entity.*"),
public class MultiTenantJpaConfiguration {
	
	// Based on:
	// https://vladmihalcea.com/hibernate-database-schema-multitenancy
	// https://tech.asimio.net/2017/01/17/Multitenant-applications-using-Spring-Boot-JPA-Hibernate-and-Postgres.html
	// https://bitbucket.org/asimio/springboot-hibernate-multitenancy/src/3aced9ec0b9fd8f3d0266302e8581f1aae2afe82/src/main/java/com/asimio/demo/config/dvdrental/MultiTenantJpaConfiguration.java?at=master&fileviewer=file-view-default
	
	
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(CurrentTenantIdentifierResolver currentTenantIdentifierResolver) {
		
		ServiceMultiTenantConnectionProvider multiTenantConnectionProvider = ServiceMultiTenantConnectionProvider.INSTANCE;

		Map<String, Object> hibernateProps = new LinkedHashMap<>();
		//hibernateProps.putAll(this.jpaProperties.getProperties());
		hibernateProps.put(Environment.MULTI_TENANT, MultiTenancyStrategy.SCHEMA);
		hibernateProps.put(Environment.MULTI_TENANT_CONNECTION_PROVIDER, multiTenantConnectionProvider);
		hibernateProps.put(Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, currentTenantIdentifierResolver);
		hibernateProps.put(Environment.NON_CONTEXTUAL_LOB_CREATION, "true"); // workaround to remove exception
		

		LocalContainerEntityManagerFactoryBean result = new LocalContainerEntityManagerFactoryBean();
		result.setPackagesToScan("br.com.kerubin.api");
		result.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		result.setJpaPropertyMap(hibernateProps);

		return result;
	}

	@Bean
	public PlatformTransactionManager tranManager(EntityManagerFactory entityManagerFactory) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactory);
		return transactionManager;
	}
	
	@Bean
	public AuditorAware<String> auditorAware() {
		return new AuditorAwareImpl();
	}

}
