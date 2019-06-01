package br.com.kerubin.api.database.core;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;


@Component
public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver {
	
	public TenantIdentifierResolver() {
		//System.out.println("TenantIdentifierResolver criado");
	}

	@Override
	public String resolveCurrentTenantIdentifier() {
		String currentTenant = ServiceContext.getTenant();
        currentTenant =  currentTenant != null ? currentTenant : ServiceContext.DEFAULT_TENANT_IDENTIFIER;
        String schemaName = getSchemaNameByTenant(currentTenant);
        return schemaName;
	}

	@Override
	public boolean validateExistingCurrentSessions() {
		return false;
	}
	
	public static String getSchemaNameByTenant(String tenant) {
		String domain = ServiceContext.getDomain();
		String service = ServiceContext.getService();
		String schemaName = tenant + "_" + domain + "_" + service;
		return schemaName;
	}

}
