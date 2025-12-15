package io.zeitwert.fm.server.config.jooq;

import org.jooq.conf.Settings;
import org.jooq.impl.DefaultConfiguration;
import org.springframework.boot.autoconfigure.jooq.DefaultConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JooqConfig {

	@Bean
	public DefaultConfigurationCustomizer customConfiguration() {
		return (DefaultConfiguration c) -> {
			// Enable optimistic locking with version fields
			c.setSettings(
					new Settings()
							.withUpdateRecordVersion(true)
							.withExecuteWithOptimisticLockingExcludeUnversioned(true)
			);
			// c.setExecuteListener(new ExecuteListener());
		};
	}

}
