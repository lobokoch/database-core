package br.com.kerubin.api.database.core;

import java.util.function.Function;

public class ServiceContext {
	
	public static final String DEFAULT_TENANT_IDENTIFIER = "kerubin";
	public static final String DEFAULT_USER = "kerubin";
	
	private static String defaultDomain;
	private static String defaultService;
	
	private static final ThreadLocal<String> TENANT_IDENTIFIER = new ThreadLocal<>();
	private static final ThreadLocal<String> USER = new ThreadLocal<>();
	private static final ThreadLocal<String> DOMAIN = new ThreadLocal<>();
	private static final ThreadLocal<String> SERVICE = new ThreadLocal<>();
	
	private static Function<String, String> defaultTenantProvider;
	
	
	public static String getUserTenant() {
		return TENANT_IDENTIFIER.get();		
	}
	
	public static String getTenant() {
		String tenant = DEFAULT_TENANT_IDENTIFIER;
		if (hasDefaultTenantProvider()) {
			tenant = defaultTenantProvider.apply(tenant);
		}
		else {
			tenant = TENANT_IDENTIFIER.get();
		}
		
		return isNotEmpty(tenant) ? tenant : DEFAULT_TENANT_IDENTIFIER;
	}
	
	public static void setDomain(String domain) {
		DOMAIN.set(domain);
	}
	
	public static String getDomain() {
		String value = DOMAIN.get();
		return isNotEmpty(value) ? value : defaultDomain;
	}
	
	public static void setService(String service) {
		SERVICE.set(service);
	}
	
	public static String getService() {
		String value = SERVICE.get();
		return isNotEmpty(value) ? value : defaultService;
	}
	
	public static void setTenant(String tenant) {
		TENANT_IDENTIFIER.set(tenant);
	}
	
	public static void clearTenant() {
		TENANT_IDENTIFIER.remove();
	}
	
	public static String getUser() {
		String user = USER.get();
		return isNotEmpty(user) ? user : DEFAULT_USER;
	}
	
	public static void setUser(String user) {
		USER.set(user);
	}
	
	public static void clearUser() {
		USER.remove();
	}
	
	public static void clear() {
		clearTenant();
		clearUser();
	}
	
	private static boolean isEmpty(String value) {
		return value == null || value.trim().isEmpty();
	}
	
	private static boolean isNotEmpty(String value) {
		return !isEmpty(value);
	}
	
	private static boolean hasDefaultTenantProvider() {
		return defaultTenantProvider != null;
	}

	public static Function<String, String> getDefaultTenantProvider() {
		return defaultTenantProvider;
	}

	public static void setDefaultTenantProvider(Function<String, String> defaultTenantProvider) {
		ServiceContext.defaultTenantProvider = defaultTenantProvider;
	}

	public static void setDefaultDomain(String domain) {
		defaultDomain = domain;
	}
	
	public static void setDefaultService(String service) {
		defaultService = service;
	}
	

}

