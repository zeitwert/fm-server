package io.zeitwert.fm.server.session.adapter.rest.dto

import io.zeitwert.app.api.jsonapi.EnumeratedDto
import io.zeitwert.app.api.jsonapi.dto.TypedEnumeratedDto

data class AuthenticateResponse(
	val id: Int,
	val name: String,
	val email: String,
	val role: EnumeratedDto,
	val tenants: List<TypedEnumeratedDto>,
)
