package io.zeitwert.server.config.jooq;

import org.jooq.impl.DefaultConfiguration;
import org.springframework.boot.autoconfigure.jooq.DefaultConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JooqConfig {

	@Bean
	public DefaultConfigurationCustomizer configurationCustomiser() {
		return (DefaultConfiguration c) -> {
			// c.setExecuteListener(new ExecuteListener());
		};
	}

}
