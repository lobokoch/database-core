package br.com.kerubin.api.database.core;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.Location;
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
	
	private Set<String> flyWayLocations = new LinkedHashSet<>();
	
	public ServiceConnectionProvider() {
		// Nothing to do for now.
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
