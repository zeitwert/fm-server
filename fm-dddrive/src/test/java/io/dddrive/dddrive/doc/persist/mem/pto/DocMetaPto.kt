package io.dddrive.domain.doc.persist.mem.pto // Adjusted package

import io.dddrive.dddrive.ddd.persist.mem.pto.AggregateMetaPto
import io.dddrive.dddrive.doc.persist.mem.pto.DocPartTransitionPto
import java.time.OffsetDateTime

open class DocMetaPto(
	var docTypeId: String? = null,
	var caseDefId: String? = null,
	var caseStageId: String? = null,
	var isInWork: Boolean? = null,
	var assigneeId: Any? = null,
	var transitions: List<DocPartTransitionPto>? = null,
	// from parent AggregateMetaPto
	maxPartId: Int? = null,
	ownerId: Any? = null,
	version: Int? = null,
	createdAt: OffsetDateTime? = null,
	createdByUserId: Any? = null,
	modifiedAt: OffsetDateTime? = null,
	modifiedByUserId: Any? = null,
) : AggregateMetaPto(
		maxPartId,
		ownerId as? Int,
		version,
		createdAt,
		createdByUserId as? Int,
		modifiedAt,
		modifiedByUserId as? Int,
	)
