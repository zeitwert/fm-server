package io.zeitwert.fm.server.config.crnk;

import io.crnk.core.boot.CrnkBoot;
import io.crnk.spring.setup.boot.core.CrnkBootConfigurer;
import io.dddrive.util.CustomFilters;

import org.springframework.context.annotation.Configuration;

@Configuration
public class CrnkBootConfig implements CrnkBootConfigurer {

	@Override
	public void configure(CrnkBoot boot) {
		DefaultQuerySpecUrlMapper mapper = (DefaultQuerySpecUrlMapper) boot.getUrlMapper();
		mapper.addSupportedOperator(CustomFilters.IN);
	}

}
