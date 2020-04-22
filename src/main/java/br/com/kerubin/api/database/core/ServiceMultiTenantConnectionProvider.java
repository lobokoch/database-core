package br.com.kerubin.api.database.core;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.hibernate.engine.jdbc.connections.spi.AbstractMultiTenantConnectionProvider;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServiceMultiTenantConnectionProvider extends AbstractMultiTenantConnectionProvider {
	
	private static final long serialVersionUID = 1L;
	
	public static final ServiceMultiTenantConnectionProvider INSTANCE = new ServiceMultiTenantConnectionProvider();
	
	private static final Map<String, ConnectionProvider> CONNECTION_PROVIDER_MAP = new ConcurrentHashMap<>();
	
	private ServiceMultiTenantConnectionProvider() {
		// System.out.println("MultiTenantConnectionProvider criado");
	}
	
	public Map<String, ConnectionProvider> getConnectionProviderMap() {
        return CONNECTION_PROVIDER_MAP;
    }
	
	private ConnectionProvider getDefaultConnectionProvider() {
		ConnectionProvider result = CONNECTION_PROVIDER_MAP.get(ServiceConnectionProvider.DEFAULT_CONNECTION_ID);
		return result;
	}

	@Override
	protected ConnectionProvider getAnyConnectionProvider() {
		// log.info("getAnyConnectionProvider");
		String schameName = TenantIdentifierResolver.getSchemaNameByTenant(ServiceContext.DEFAULT_TENANT_IDENTIFIER);
		ServiceConnectionProvider.INSTANCE.addTenantConnectionProvider(schameName);
		return getDefaultConnectionProvider();
	}

	@Override
	protected ConnectionProvider selectConnectionProvider(String tenantIdentifier) {
		// log.info("selectConnectionProvider for tenant: {}.", tenantIdentifier);
		ServiceConnectionProvider.INSTANCE.addTenantConnectionProvider(tenantIdentifier);
		ConnectionProvider connection = getDefaultConnectionProvider();
		return connection;
	}
	
	@Override
	public Connection getConnection(String tenantIdentifier) throws SQLException {
		// log.info("getConnection for tenant: {}.", tenantIdentifier);
		Connection connection = super.getConnection(tenantIdentifier);
		
		try {
			try (Statement stmt = connection.createStatement()) {
				//String schemaName = TenantIdentifierResolver.getSchemaNameByTenant(tenantIdentifier);
				stmt.execute(getSchemaChangeCommand(tenantIdentifier));
			}
			return connection;
		} catch (Exception e) {
			releaseConnection(tenantIdentifier, connection);
			throw e;
		}
		
	}
	
	@Override
	public Connection getAnyConnection() throws SQLException {
		// log.info("getAnyConnection");
		return super.getAnyConnection();
	}
	
	
	@Override
	public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
		// log.info("releaseConnection for tenant: {}", tenantIdentifier);
		super.releaseConnection(tenantIdentifier, connection);
	}
	
	@Override
	public void releaseAnyConnection(Connection connection) throws SQLException {
		// log.info("releaseAnyConnection.");
		super.releaseAnyConnection(connection);
	}
	
    public String getSchemaChangeCommand(String schema) {
        String result = MessageFormat.format("SET SEARCH_PATH TO ''{0}'';", schema);
        log.info("Changing schema to: {}", result);
        return result;
    }
}
