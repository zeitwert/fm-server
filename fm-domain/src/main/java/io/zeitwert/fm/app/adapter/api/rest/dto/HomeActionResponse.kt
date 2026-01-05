package io.zeitwert.fm.app.adapter.api.rest.dto

import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto.EnumeratedDto
import java.time.OffsetDateTime

data class HomeActionResponse(
	val item: EnumeratedDto,
	val seqNr: Int,
	val timestamp: OffsetDateTime,
	val user: EnumeratedDto,
	val changes: String? = null,
	val oldCaseStage: EnumeratedDto? = null,
	val newCaseStage: EnumeratedDto,
)
