package io.zeitwert.fm.app.adapter.api.rest.dto

import io.zeitwert.dddrive.ddd.api.rest.dto.EnumeratedDto

data class TenantInfoResponse(
	val id: Int,
	val tenantType: EnumeratedDto,
	val accounts: List<EnumeratedDto>,
)
