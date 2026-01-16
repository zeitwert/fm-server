package io.zeitwert.app.api.rest.dto

import io.zeitwert.app.api.jsonapi.EnumeratedDto
import io.zeitwert.app.api.jsonapi.dto.TypedEnumeratedDto

data class HomeActivityResponse(
	val item: TypedEnumeratedDto, // rating, task
	val relatedTo: TypedEnumeratedDto, // rating: building, task: obj
	val owner: TypedEnumeratedDto? = null,
	val user: TypedEnumeratedDto? = null,
	val dueAt: String? = null,
	val subject: String? = null,
	val content: String? = null,
	val priority: EnumeratedDto? = null,
)
