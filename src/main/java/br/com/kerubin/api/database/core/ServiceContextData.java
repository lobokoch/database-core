package br.com.kerubin.api.database.core;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ServiceContextData {
	
	private String tenant;
	private String tenantAccountType;
	private String username;
	private String domain;
	private String service;
	
}
