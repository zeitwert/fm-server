package dddrive.domain.ddd.persist.mem.pto

import java.time.OffsetDateTime

open class AggregateMetaPto(
	var maxPartId: Int? = null,
	var ownerId: Int? = null,
	var version: Int? = null,
	var createdAt: OffsetDateTime? = null,
	var createdByUserId: Int? = null,
	var modifiedAt: OffsetDateTime? = null,
	var modifiedByUserId: Int? = null,
)
