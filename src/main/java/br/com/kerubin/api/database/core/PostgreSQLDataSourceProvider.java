package br.com.kerubin.api.database.core;

import static br.com.kerubin.api.database.util.Utils.getProp;
import static br.com.kerubin.api.database.util.Utils.getPropInt;

import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.dialect.PostgreSQL95Dialect;
import org.postgresql.ds.PGSimpleDataSource;

public class PostgreSQLDataSourceProvider implements DataSourceProvider {
	
	private static final String DB_NAME_KEY = "DB_NAME";
	private static final String DB_USER_KEY = "DB_USER";
	private static final String DB_PASSWORD_KEY = "DB_PASSWORD";
	private static final String DB_SERVER_NAME_KEY = "DB_HOST";
	private static final String DB_PORT_NUMBER_KEY = "DB_PORT";
	
	private static final String DB_NAME = "kerubin_aws";
	private static final String DB_USER = "kb_lobo_mk_181269";
	private static final String DB_PASSWORD = "sNiwCau1896_LzP";
	private static final String DB_SERVER_NAME = "localhost";
	private static final int DB_PORT_NUMBER = 5432;
	
	public PostgreSQLDataSourceProvider() {
		// 
	}
	
    @Override
    public String hibernateDialect() {
        return PostgreSQL95Dialect.class.getName();
    }

    @Override
    public DataSource dataSource() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setServerName(getHost());
        dataSource.setPortNumber(getPortNumber());
        dataSource.setDatabaseName(getDatabaseName());
        dataSource.setUser(getUser());
        dataSource.setPassword(getPassword());
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
        properties.setProperty("serverName", getHost());
        properties.setProperty("user", getUser());
        properties.setProperty("password", getPassword());
        return properties;
    }
    
    @Override
    public String getDatabaseName() {
    	String databaseName = getProp(DB_NAME_KEY, DB_NAME);
    	return databaseName;
    }
    
    @Override
    public int getPortNumber() {
    	int portNumber = getPropInt(DB_PORT_NUMBER_KEY, DB_PORT_NUMBER);
    	return portNumber;
    }
    
    @Override
    public String getHost() {
    	String host = getProp(DB_SERVER_NAME_KEY, DB_SERVER_NAME);
    	return host;
    }

    @Override
    public String getUrl() {
        return null;
    }

    @Override
    public String getUser() {
    	String username = getProp(DB_USER_KEY, DB_USER);
        return username;
    }

    @Override
    public String getPassword() {
    	String password = getProp(DB_PASSWORD_KEY, DB_PASSWORD);
        return password;
    }

    @Override
    public Database getDatabase() {
        return Database.POSTGRESQL;
    }
    
}