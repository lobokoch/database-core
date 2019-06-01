package br.com.kerubin.api.database.core;

import org.springframework.beans.BeanUtils;

public enum Database {
	//HSQLDB(HSQLDBDataSourceProvider.class),
	POSTGRESQL(PostgreSQLDataSourceProvider.class);
	//,ORACLE(OracleDataSourceProvider.class),
	//MYSQL(MySQLDataSourceProvider.class),
	//MARIADB(MariaDBDataSourceProvider.class),
	//SQLSERVER(SQLServerDataSourceProvider.class),
	//COCKROACHDB(CockroachDBDataSourceProvider.class);

	private Class<? extends DataSourceProvider> dataSourceProviderClass;

	Database(Class<? extends DataSourceProvider> dataSourceProviderClass) {
		this.dataSourceProviderClass = dataSourceProviderClass;
	}

	public DataSourceProvider dataSourceProvider() {
		return BeanUtils.instantiateClass(dataSourceProviderClass);
		//return ReflectionUtils.newInstance(dataSourceProviderClass.getName());
	}
}
