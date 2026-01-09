package io.zeitwert.fm.server.session.adapter.rest.dto

data class ActivateRequest(
	val tenantId: Int,
	val accountId: Int? = null,
)
