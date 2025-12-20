package io.zeitwert.fm.server.session.adapter.rest.dto

data class LoginRequest(
	val email: String? = null,
	val password: String? = null,
	val tenantId: Int? = null,
	val accountId: Int? = null
)
