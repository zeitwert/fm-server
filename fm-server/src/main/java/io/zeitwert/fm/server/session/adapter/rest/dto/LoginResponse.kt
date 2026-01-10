package io.zeitwert.fm.server.session.adapter.rest.dto

import io.zeitwert.dddrive.api.jsonapi.dto.EnumeratedDto

data class LoginResponse(
	val sessionId: String? = null,
	val id: Int? = null,
	val username: String? = null,
	val email: String? = null,
	val accountId: Int? = null,
	val role: EnumeratedDto? = null,
)
