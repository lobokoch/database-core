package br.com.kerubin.api.database.core;

import java.util.Properties;

import javax.sql.DataSource;

public interface DataSourceProvider {
	
	enum IdentifierStrategy {
		IDENTITY,
		SEQUENCE
	}

	String hibernateDialect();

	DataSource dataSource();

	Class<? extends DataSource> dataSourceClassName();

	Properties dataSourceProperties();

	String url();

	String username();

	String password();

	Database database();

}
