package io.zeitwert.fm.server.session.adapter.rest.dto

import io.zeitwert.app.api.jsonapi.EnumeratedDto

data class AuthenticateResponse(
	val id: Int,
	val name: String,
	val email: String,
	val role: EnumeratedDto,
	val tenants: List<EnumeratedDto>,
)
