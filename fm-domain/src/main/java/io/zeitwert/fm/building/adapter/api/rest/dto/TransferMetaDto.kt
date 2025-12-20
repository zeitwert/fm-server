package io.zeitwert.fm.building.adapter.api.rest.dto

import java.time.OffsetDateTime

data class TransferMetaDto(
    var aggregate: String? = null,
    var version: String? = null,
    var createdByUser: String? = null,
    var createdAt: OffsetDateTime? = null,
    var modifiedByUser: String? = null,
    var modifiedAt: OffsetDateTime? = null,
)

