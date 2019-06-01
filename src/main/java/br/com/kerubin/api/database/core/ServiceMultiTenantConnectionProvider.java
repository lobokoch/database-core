package br.com.kerubin.api.database.core;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.engine.jdbc.connections.spi.AbstractMultiTenantConnectionProvider;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;


public class ServiceMultiTenantConnectionProvider extends AbstractMultiTenantConnectionProvider {
	
	private static final long serialVersionUID = 1L;
	
	public static final ServiceMultiTenantConnectionProvider INSTANCE = new ServiceMultiTenantConnectionProvider();
	
	private final Map<String, ConnectionProvider> connectionProviderMap = new HashMap<>();
	
	private ServiceMultiTenantConnectionProvider() {
		// System.out.println("MultiTenantConnectionProvider criado");
	}
	
	Map<String, ConnectionProvider> getConnectionProviderMap() {
        return connectionProviderMap;
    }

	@Override
	protected ConnectionProvider getAnyConnectionProvider() {
		// System.out.println("getAnyConnectionProvider");
		String schameName = TenantIdentifierResolver.getSchemaNameByTenant(ServiceContext.DEFAULT_TENANT_IDENTIFIER);
		ServiceConnectionProvider.INSTANCE.addTenantConnectionProvider(schameName);
		return connectionProviderMap.get(schameName);
	}

	@Override
	protected ConnectionProvider selectConnectionProvider(String tenantIdentifier) {
		// System.out.println("selectConnectionProvider:" + tenantIdentifier);
		ServiceConnectionProvider.INSTANCE.addTenantConnectionProvider(tenantIdentifier);
		ConnectionProvider connection = connectionProviderMap.get(tenantIdentifier);
		return connection;
	}
	
	@Override
	public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
		//String schameName = getSchemaName(tenantIdentifier);
		super.releaseConnection(tenantIdentifier, connection);
	}
	
	@Override
	public void releaseAnyConnection(Connection connection) throws SQLException {
		super.releaseAnyConnection(connection);
	}
	

}
