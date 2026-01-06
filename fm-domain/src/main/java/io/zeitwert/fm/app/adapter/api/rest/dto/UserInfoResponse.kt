package io.zeitwert.fm.app.adapter.api.rest.dto

import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto.EnumeratedDto
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto.TypedEnumeratedDto

data class UserInfoResponse(
	val id: Int,
	val name: String,
	val email: String,
	val role: EnumeratedDto,
	val tenants: List<TypedEnumeratedDto>,
)
