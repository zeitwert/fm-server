package io.zeitwert.fm.server.config.crnk;

import org.springframework.stereotype.Component;

import io.crnk.core.boot.CrnkBoot;
import io.crnk.core.queryspec.mapper.QuerySpecUrlMapper;
import io.dddrive.util.CustomFilters;

/**
 * Custom CrnkBoot configurer that adds support for the IN filter operator.
 */
@Component
public class CrnkBootConfig implements CrnkBootConfigurer {

	@Override
	public void configure(CrnkBoot boot) {
		QuerySpecUrlMapper urlMapper = boot.getUrlMapper();
		if (urlMapper instanceof DefaultQuerySpecUrlMapper mapper) {
			mapper.addSupportedOperator(CustomFilters.IN);
		}
	}
}
