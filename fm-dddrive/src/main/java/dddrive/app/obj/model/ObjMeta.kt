package dddrive.app.obj.model

import dddrive.app.validation.model.AggregatePartValidation
import dddrive.ddd.core.model.AggregateMeta
import java.time.OffsetDateTime

interface ObjMeta : AggregateMeta {

	val objTypeId: String

	// val createdByUser: ObjUser
	val createdByUserId: Any
	val createdAt: OffsetDateTime

	// val modifiedByUser: ObjUser?
	val modifiedByUserId: Any?
	val modifiedAt: OffsetDateTime?

	// val closedByUser: ObjUser?
	val closedByUserId: Any?
	val closedAt: OffsetDateTime?

	val transitionList: List<ObjPartTransition>

	val validationList: List<AggregatePartValidation>

}
