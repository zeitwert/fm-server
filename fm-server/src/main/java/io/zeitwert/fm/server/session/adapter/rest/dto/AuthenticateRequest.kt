package io.zeitwert.fm.server.session.adapter.rest.dto

data class AuthenticateRequest(
	val email: String,
	val password: String,
)
