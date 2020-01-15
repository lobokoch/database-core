package br.com.kerubin.api.database.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

//@Configuration
@ConfigurationProperties("kerubin.db")
@Getter
@Setter
@ToString
public class DatabaseConfig {
	
	public DatabaseConfig() {
		System.out.println("DatabaseConfig created.");
	}
	
	private String serverName;
	private int portNumber;
	private String databaseName;
	private String user;
	private String password;

}
