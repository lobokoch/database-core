package br.com.kerubin.api.database.core;

import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.dialect.PostgreSQL95Dialect;
import org.postgresql.ds.PGSimpleDataSource;

public class PostgreSQLDataSourceProvider implements DataSourceProvider {
	
	private static final String DB_NAME = "kerubin";
	private static final String DB_USER = "postgres";
	private static final String DB_PASSWORD = "mk";
	private static final String DB_SERVER_NAME = "localhost";

    @Override
    public String hibernateDialect() {
        return PostgreSQL95Dialect.class.getName();
    }

    @Override
    public DataSource dataSource() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setDatabaseName(DB_NAME);
        dataSource.setServerName(DB_SERVER_NAME);
        dataSource.setUser(DB_USER);
        dataSource.setPassword(DB_PASSWORD);
        return dataSource;
    }

    @Override
    public Class<? extends DataSource> dataSourceClassName() {
        return PGSimpleDataSource.class;
    }

    @Override
    public Properties dataSourceProperties() {
        Properties properties = new Properties();
        properties.setProperty("databaseName", DB_NAME);
        properties.setProperty("serverName", DB_SERVER_NAME);
        properties.setProperty("user", username());
        properties.setProperty("password", password());
        return properties;
    }

    @Override
    public String url() {
        return null;
    }

    @Override
    public String username() {
        return DB_USER;
    }

    @Override
    public String password() {
        return DB_PASSWORD;
    }

    @Override
    public Database database() {
        return Database.POSTGRESQL;
    }
    
}