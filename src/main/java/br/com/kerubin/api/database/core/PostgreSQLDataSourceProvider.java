package br.com.kerubin.api.database.core;

import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.dialect.PostgreSQL95Dialect;
import org.postgresql.ds.PGSimpleDataSource;
import static br.com.kerubin.api.database.util.Utils.*;

public class PostgreSQLDataSourceProvider implements DataSourceProvider {
	
	private static final String DB_NAME_KEY = "DB_NAME";
	private static final String DB_USER_KEY = "DB_USER";
	private static final String DB_PASSWORD_KEY = "DB_PASSWORD";
	private static final String DB_SERVER_NAME_KEY = "DB_HOST";
	
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
        dataSource.setDatabaseName(getDatabaseName());
        dataSource.setServerName(getDatabaseHost());
        dataSource.setUser(username());
        dataSource.setPassword(password());
        return dataSource;
    }

    @Override
    public Class<? extends DataSource> dataSourceClassName() {
        return PGSimpleDataSource.class;
    }

    @Override
    public Properties dataSourceProperties() {
        Properties properties = new Properties();
        properties.setProperty("databaseName", getDatabaseName());
        properties.setProperty("serverName", getDatabaseHost());
        properties.setProperty("user", username());
        properties.setProperty("password", password());
        return properties;
    }
    
    public String getDatabaseName() {
    	String databaseName = getEnvDef(DB_NAME_KEY, DB_NAME);
    	return databaseName;
    }
    
    public String getDatabaseHost() {
    	String host = getEnvDef(DB_SERVER_NAME_KEY, DB_SERVER_NAME);
    	return host;
    }

    @Override
    public String url() {
        return null;
    }

    @Override
    public String username() {
    	String username = System.getProperty(DB_USER_KEY, DB_USER);
        return username;
    }

    @Override
    public String password() {
    	String password = getEnvDef(DB_PASSWORD_KEY, DB_PASSWORD);
        return password;
    }

    @Override
    public Database database() {
        return Database.POSTGRESQL;
    }
    
}