package io.zeitwert.dddrive.model

import io.crnk.core.queryspec.QuerySpec
import io.zeitwert.fm.app.model.RequestContextFM

interface FMAggregateRepository {

	fun find(
		query: QuerySpec?,
		requestContext: RequestContextFM,
	): List<Any>

}
