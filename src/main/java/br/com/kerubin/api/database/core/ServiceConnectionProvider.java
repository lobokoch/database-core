package br.com.kerubin.api.database.core;

import java.util.Properties;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.hibernate.MultiTenancyStrategy;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Environment;
import org.hibernate.engine.jdbc.connections.internal.DatasourceConnectionProviderImpl;
import org.postgresql.ds.PGSimpleDataSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServiceConnectionProvider extends BaseConnectionProvider {
	
	public static final ServiceConnectionProvider INSTANCE = new ServiceConnectionProvider();
	
	private boolean migrateDefaultTenant;
	
	public ServiceConnectionProvider() {
		//System.out.println("ServiceConnectionProvider criado");
	}
	
	public void addTenantConnectionProvider(String tenantId) {
		
		if ( ServiceMultiTenantConnectionProvider.INSTANCE.getConnectionProviderMap().containsKey(tenantId)) {
			return;
		}
		
        PGSimpleDataSource defaultDataSource = (PGSimpleDataSource) database().dataSourceProvider().dataSource();

        PGSimpleDataSource tenantDataSource = new PGSimpleDataSource();
        tenantDataSource.setDatabaseName(defaultDataSource.getDatabaseName());
        tenantDataSource.setCurrentSchema(tenantId);
        tenantDataSource.setServerName(defaultDataSource.getServerName());
        tenantDataSource.setUser(defaultDataSource.getUser());
        tenantDataSource.setPassword(defaultDataSource.getPassword());

        Properties properties = properties();
        properties.put(
                Environment.DATASOURCE,
                /*dataSourceProxyType().dataSource(*/tenantDataSource/*)*/
        );

        addTenantConnectionProvider(tenantId, tenantDataSource, properties);
        flywayMigrateTenant(tenantId, tenantDataSource);
    }
    
    private void flywayMigrateTenant(String tenantId, DataSource tenantDataSource) {
    	String defaultSchemaName = TenantIdentifierResolver.getSchemaNameByTenant(ServiceContext.DEFAULT_TENANT_IDENTIFIER);
    	if (!defaultSchemaName.equals(tenantId) || isMigrateDefaultTenant()) {
    		try {
    			log.info("Starting Flyway migration for tenant: {}...", tenantId);
	    		
    			FluentConfiguration flywayConfig = Flyway.configure();
	    		flywayConfig.dataSource(tenantDataSource).schemas(tenantId);
	    		Flyway flyway = flywayConfig.load();
	    		flyway.migrate();
	    		
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
        //System.out.println(ServiceMultiTenantConnectionProvider.INSTANCE.getConnectionProviderMap());
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
