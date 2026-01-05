package io.zeitwert.fm.app.adapter.api.rest.dto

import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto.EnumeratedDto

data class HomeActivityResponse(
	val item: EnumeratedDto, // rating, task
	val relatedTo: EnumeratedDto, // rating: building, task: obj
	val owner: EnumeratedDto? = null,
	val user: EnumeratedDto? = null,
	val dueAt: String? = null,
	val subject: String? = null,
	val content: String? = null,
	val priority: EnumeratedDto? = null,
)
