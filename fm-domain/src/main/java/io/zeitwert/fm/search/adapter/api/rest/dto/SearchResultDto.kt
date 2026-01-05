package io.zeitwert.fm.search.adapter.api.rest.dto

import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto.EnumeratedDto
import java.math.BigDecimal

data class SearchResultDto(
	val tenantId: Int,
	val itemType: EnumeratedDto,
	val id: String,
	val caption: String,
	val rank: BigDecimal,
)
