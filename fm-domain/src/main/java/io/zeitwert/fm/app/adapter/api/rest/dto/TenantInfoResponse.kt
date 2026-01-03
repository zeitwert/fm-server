package io.zeitwert.fm.app.adapter.api.rest.dto

import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto.EnumeratedDto

data class TenantInfoResponse(
	val id: Int,
	val tenantType: EnumeratedDto,
	val accounts: List<EnumeratedDto>,
)
