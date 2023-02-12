
package io.zeitwert.fm.server.config.crnk;

import org.springframework.context.annotation.Configuration;

@Configuration
public class DefaultQuerySpecUrlMapper extends io.crnk.core.queryspec.mapper.DefaultQuerySpecUrlMapper {

	public DefaultQuerySpecUrlMapper() {
		super();
		this.pathResolver = new DefaultQueryPathResolver();
	}

}
