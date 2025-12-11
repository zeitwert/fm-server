package io.zeitwert.dddrive.ddd.api.rest.dto

import io.dddrive.core.ddd.model.Aggregate

interface AggregateDto<A : Aggregate> {
	fun getMeta(): AggregateMetaDto?

	fun getId(): String?

	fun getExtn(): Map<String, Any>?

	// Read: for orderbooks, write: for creation
	fun getTenant(): EnumeratedDto?

	// Read: for orderbooks, write: for updates
	fun getOwner(): EnumeratedDto?

	fun getCaption(): String?

	// For explicit filtering in SaaS session
	fun getTenantId(): String? = null

	fun getSearchText(): String? = null
}
