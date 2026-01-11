package io.zeitwert.fm.server.session.adapter.rest.dto

import io.zeitwert.fm.account.adapter.jsonapi.dto.ObjAccountDto
import io.zeitwert.fm.oe.adapter.jsonapi.dto.ObjTenantDto
import io.zeitwert.fm.oe.adapter.jsonapi.dto.ObjUserDto

data class SessionInfoResponse(
	val user: ObjUserDto? = null,
	val tenant: ObjTenantDto? = null,
	val account: ObjAccountDto? = null,
	val locale: String? = null,
	val applicationId: String? = null,
	val applicationName: String? = null,
	val applicationVersion: String? = null,
	val availableApplications: List<String>? = null,
)
