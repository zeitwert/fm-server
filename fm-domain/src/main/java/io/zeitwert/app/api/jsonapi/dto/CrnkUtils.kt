package io.zeitwert.app.api.jsonapi.dto

import io.crnk.core.queryspec.FilterSpec
import io.crnk.core.queryspec.QuerySpec

object CrnkUtils {

	fun hasFilterFor(
		querySpec: QuerySpec,
		fieldName: String,
	): Boolean = querySpec.filters.any { getPath(it) == fieldName }

	@JvmStatic
	fun getPath(filter: FilterSpec): String =
		filter.path.elements
			.joinToString(".")
			.replace(".id", "Id")

}
