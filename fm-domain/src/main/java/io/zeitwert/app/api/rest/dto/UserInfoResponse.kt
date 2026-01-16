package io.zeitwert.app.api.rest.dto

import io.zeitwert.app.api.jsonapi.EnumeratedDto

data class UserInfoResponse(
	val id: Int,
	val name: String,
	val email: String,
	val role: EnumeratedDto,
	val tenants: List<EnumeratedDto>,
)
