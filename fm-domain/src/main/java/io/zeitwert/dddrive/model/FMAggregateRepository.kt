package io.zeitwert.dddrive.model

import io.crnk.core.queryspec.QuerySpec

interface FMAggregateRepository {

	fun find(query: QuerySpec?): List<Any>

}
