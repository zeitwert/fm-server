package io.zeitwert.fm.building.api.rest.dto

import java.time.OffsetDateTime

data class NoteTransferDto(
	var subject: String? = null,
	var content: String? = null,
	var isPrivate: Boolean? = null,
	var createdByUser: String? = null,
	var createdAt: OffsetDateTime? = null,
	var modifiedByUser: String? = null,
	var modifiedAt: OffsetDateTime? = null,
)
