package io.zeitwert.app.api.rest.dto

import io.zeitwert.dddrive.api.jsonapi.dto.EnumeratedDto
import io.zeitwert.dddrive.api.jsonapi.dto.TypedEnumeratedDto
import java.time.OffsetDateTime

data class HomeActionResponse(
	val item: TypedEnumeratedDto,
	val seqNr: Int,
	val timestamp: OffsetDateTime,
	val user: TypedEnumeratedDto,
	val changes: String? = null,
	val oldCaseStage: EnumeratedDto? = null,
	val newCaseStage: EnumeratedDto? = null,
)
