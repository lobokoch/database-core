package br.com.kerubin.api.database.core;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class BaseConnectionProvider {
	
	private List<Closeable> closeables = new ArrayList<>();
	
	protected DataSourceProvider dataSourceProvider() {
        return database().dataSourceProvider();
    }

    protected Database database() {
        return Database.POSTGRESQL;
    }
    
    protected DataSource newDataSource() {
        DataSource dataSource = dataSourceProvider().dataSource();
        if(connectionPooling()) {
            HikariDataSource poolingDataSource = connectionPoolDataSource(dataSource);
            closeables.add(poolingDataSource::close);
            return poolingDataSource;
        } else {
            return dataSource;
        }
    }
    
    protected boolean connectionPooling() {
        return true;
    }
    
    /*protected DataSourceProxyType dataSourceProxyType() {
        return DataSourceProxyType.DATA_SOURCE_PROXY;
    }*/

    
    protected boolean proxyDataSource() {
        return false;
    }
    
    protected HikariDataSource connectionPoolDataSource(DataSource dataSource) {
        return new HikariDataSource(hikariConfig(dataSource));
    }

    protected HikariConfig hikariConfig(DataSource dataSource) {
        HikariConfig hikariConfig = new HikariConfig();
        int cpuCores = Runtime.getRuntime().availableProcessors();
        hikariConfig.setMaximumPoolSize(cpuCores * 2);
        hikariConfig.setMinimumIdle(0);
        
        hikariConfig.setIdleTimeout(60000);
        hikariConfig.setDataSource(dataSource);
        return hikariConfig;
    }

	protected Properties properties() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", dataSourceProvider().hibernateDialect());
        DataSource dataSource = newDataSource();
        if (dataSource != null) {
            properties.put("hibernate.connection.datasource", dataSource);
        }
        properties.put("hibernate.generate_statistics", Boolean.TRUE.toString());

        //properties.put("net.sf.ehcache.configurationResourceName", Thread.currentThread().getContextClassLoader().getResource("ehcache.xml").toString());
        //properties.put("hibernate.ejb.metamodel.population", "disabled");
        additionalProperties(properties);
        return properties;
    }

    protected void additionalProperties(Properties properties) {

    }
    
    public void destroy() {
        
        for(Closeable closeable : closeables) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        closeables.clear();
    }
    
    protected void finalize() throws Throwable {  
        try { 
        	destroy(); 
        } 
        catch (Exception e) { 
            e.printStackTrace();
        }
        super.finalize();  
    }  

}
