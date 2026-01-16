package io.zeitwert.app.search.api.rest.dto

import io.zeitwert.app.api.jsonapi.EnumeratedDto
import java.math.BigDecimal

data class SearchResultDto(
	val tenantId: Int,
	val itemType: EnumeratedDto,
	val id: String,
	val caption: String,
	val rank: BigDecimal,
)
