package br.com.kerubin.api.database.core;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.Location;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.hibernate.MultiTenancyStrategy;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Environment;
import org.hibernate.engine.jdbc.connections.internal.DatasourceConnectionProviderImpl;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServiceConnectionProvider extends BaseConnectionProvider {
	
	public static final ServiceConnectionProvider INSTANCE = new ServiceConnectionProvider();
	
	public static final String DEFAULT_CONNECTION_ID = "DEFAULT_CONNECTION_ID";
	
	private boolean migrateDefaultTenant;
	
	private Set<String> flyWayLocations = new LinkedHashSet<>();
	private static final Map<String, Object> MIGRATION_SCHEMAS = new ConcurrentHashMap<>(); 
	
	private static final Lock lockDataSource = new ReentrantLock();
	private static final Lock lockMigration = new ReentrantLock();
	
	public ServiceConnectionProvider() {
		// Nothing to do for now.
	}
	
	public void addTenantConnectionProvider(String tenantId) {
		DataSource dataSource = null;
		
		if (!ServiceMultiTenantConnectionProvider.INSTANCE.getConnectionProviderMap().containsKey(DEFAULT_CONNECTION_ID)) {
			
			lockDataSource.lock();
			try {
				if (!ServiceMultiTenantConnectionProvider.INSTANCE.getConnectionProviderMap().containsKey(DEFAULT_CONNECTION_ID)) {
					dataSource = database().dataSourceProvider().dataSource();
					Properties properties = properties();
					properties.put(Environment.DATASOURCE, dataSource);
					addTenantConnectionProvider(DEFAULT_CONNECTION_ID, dataSource, properties);
				} // if
			} finally {
				lockDataSource.unlock();
			}
			
		} // if
		
		if (!MIGRATION_SCHEMAS.containsKey(tenantId)) {
			
			lockMigration.lock();
			try {
				
				if (!MIGRATION_SCHEMAS.containsKey(tenantId)) {
					if (dataSource == null) {
						ConnectionProvider connectionProvider = ServiceMultiTenantConnectionProvider.INSTANCE
								.getConnectionProviderMap().get(DEFAULT_CONNECTION_ID);
						
						dataSource = ((DatasourceConnectionProviderImpl) connectionProvider).getDataSource();
					}
					
					flywayMigrateTenant(tenantId, dataSource);
					MIGRATION_SCHEMAS.put(tenantId, tenantId);
				} // if
			} finally {
				lockMigration.unlock();
			}
			
		} // if
    }
    
    private void flywayMigrateTenant(String tenantId, DataSource tenantDataSource) {
    	String defaultSchemaName = TenantIdentifierResolver.getSchemaNameByTenant(ServiceContext.DEFAULT_TENANT_IDENTIFIER);
    	if (!defaultSchemaName.equals(tenantId) || isMigrateDefaultTenant()) {
    		try {
    			log.info("Starting Flyway migration for tenant: {}...", tenantId);
    			flyWayLocations.clear();
	    		
    			FluentConfiguration flywayConfig = Flyway.configure();
    			
    			String domain = ServiceContext.getDomain();
    			String service = ServiceContext.getService();
    			String accountType = ServiceContext.getTenatAccountType();
    			
    			String commonLocation = MessageFormat.format("classpath:db/migration/{0}/{1}/PostgreSql/common", domain, service);
    			String accountTypeLocation = MessageFormat.format("classpath:db/migration/{0}/{1}/PostgreSql/{2}", domain, service, accountType);
    			flyWayLocations.add(commonLocation);
    			flyWayLocations.add(accountTypeLocation);
    			
    			if (!flyWayLocations.isEmpty()) {
    				flywayConfig.locations(flyWayLocations.toArray(new String[0]));
    			}
    			
    			Location[] locations = flywayConfig.getLocations();
    			log.info("flywayConfig.getLocations:" + Arrays.asList(locations));
    			
	    		flywayConfig.dataSource(tenantDataSource).schemas(tenantId);
	    		Flyway flyway = flywayConfig.load();
	    		flyway.migrate();
	    		
	    		flyWayLocations.clear();
	    		log.info("DONE! Flyway migration for tenant: {}...", tenantId);
    		}
    		catch (Exception e) {
    			log.error("Flyway migration for tenant: " + tenantId + " failed with error: " + e.getMessage(), e);
    			throw e;
    		}
    	}
		
	}

	private void addTenantConnectionProvider(String tenantId, DataSource tenantDataSource, Properties properties) {
        DatasourceConnectionProviderImpl connectionProvider = new DatasourceConnectionProviderImpl();
        connectionProvider.setDataSource(tenantDataSource);
        connectionProvider.configure(properties);
        ServiceMultiTenantConnectionProvider.INSTANCE.getConnectionProviderMap().put(tenantId, connectionProvider);
    }

    @Override
	protected void additionalProperties(Properties properties) {
	    properties.setProperty(AvailableSettings.HBM2DDL_AUTO, "none");
	    properties.setProperty(AvailableSettings.MULTI_TENANT, MultiTenancyStrategy.SCHEMA.name());
	    properties.setProperty(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, TenantIdentifierResolver.class.getName());
	    properties.put(AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER, ServiceMultiTenantConnectionProvider.INSTANCE);
	}

	public boolean isMigrateDefaultTenant() {
		return migrateDefaultTenant;
	}

	public void setMigrateDefaultTenant(boolean migrateDefaultTenant) {
		this.migrateDefaultTenant = migrateDefaultTenant;
	}
    

}
