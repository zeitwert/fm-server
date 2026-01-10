package io.zeitwert.fm.server.config.crnk

import io.crnk.core.boot.CrnkBoot
import io.zeitwert.persist.sql.ddd.util.CustomFilters
import org.springframework.stereotype.Component

/**
 * Custom CrnkBoot configurer that adds support for the IN filter operator.
 */
@Component
class CrnkBootConfig : CrnkBootConfigurer {

	override fun configure(boot: CrnkBoot) {
		val urlMapper = boot.getUrlMapper()
		if (urlMapper is DefaultQuerySpecUrlMapper) {
			urlMapper.addSupportedOperator(CustomFilters.IN)
		}
	}

}
