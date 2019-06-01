package br.com.kerubin.api.database.entity;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;

import br.com.kerubin.api.database.core.ServiceContext;

public class AuditorAwareImpl implements AuditorAware<String> {

	@Override
	public Optional<String> getCurrentAuditor() {
		return Optional.of(ServiceContext.getUser());
	}

}
