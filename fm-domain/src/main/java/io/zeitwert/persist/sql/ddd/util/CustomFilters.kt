package io.zeitwert.persist.sql.ddd.util

import io.crnk.core.queryspec.FilterOperator

object CustomFilters {

	@JvmField
	var IN: FilterOperator = object : FilterOperator("IN") {
		override fun matches(
			value1: Any?,
			value2: Any?,
		): Boolean {
			throw UnsupportedOperationException() // handle differently
		}
	}

}
