package dddrive.domain.obj.persist.mem.pto

import dddrive.domain.ddd.persist.mem.pto.AggregateMetaPto
import java.time.OffsetDateTime

open class ObjMetaPto(
	var objTypeId: String? = null,
	var closedAt: OffsetDateTime? = null,
	var closedByUserId: Int? = null,
	var transitions: List<ObjPartTransitionPto>? = null,
	// from parent
	maxPartId: Int? = null,
	ownerId: Int? = null,
	version: Int? = null,
	createdAt: OffsetDateTime? = null,
	createdByUserId: Int? = null,
	modifiedAt: OffsetDateTime? = null,
	modifiedByUserId: Int? = null,
) : AggregateMetaPto(
		maxPartId,
		ownerId,
		version,
		createdAt,
		createdByUserId,
		modifiedAt,
		modifiedByUserId,
	)
