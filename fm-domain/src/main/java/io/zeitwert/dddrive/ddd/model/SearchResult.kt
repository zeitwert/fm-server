package io.zeitwert.dddrive.ddd.model

import dddrive.ddd.model.enums.CodeAggregateType
import java.math.BigDecimal

data class SearchResult(
	val tenantId: Int,
	val aggregateType: CodeAggregateType,
	val id: Int,
	val caption: String,
	val rank: BigDecimal,
) : Comparable<SearchResult> {

	override fun compareTo(other: SearchResult): Int = this.rank.compareTo(other.rank)

}
