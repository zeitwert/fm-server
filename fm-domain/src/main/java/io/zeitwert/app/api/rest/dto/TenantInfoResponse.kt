package io.zeitwert.app.api.rest.dto

import io.zeitwert.app.api.jsonapi.EnumeratedDto

data class TenantInfoResponse(
	val id: Int,
	val tenantType: EnumeratedDto,
	val accounts: List<EnumeratedDto>,
)
