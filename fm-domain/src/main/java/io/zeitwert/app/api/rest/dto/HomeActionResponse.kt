package io.zeitwert.app.api.rest.dto

import io.zeitwert.app.api.jsonapi.EnumeratedDto
import java.time.OffsetDateTime

data class HomeActionResponse(
	val item: EnumeratedDto,
	val seqNr: Int,
	val timestamp: OffsetDateTime,
	val user: EnumeratedDto,
	val changes: String? = null,
	val oldCaseStage: EnumeratedDto? = null,
	val newCaseStage: EnumeratedDto? = null,
)
