package dddrive.app.ddd.model

import dddrive.app.validation.model.AggregatePartValidation
import java.time.OffsetDateTime

interface AggregateMeta : dddrive.ddd.core.model.AggregateMeta {

	// val createdByUser: ObjUser
	val createdByUserId: Any
	val createdAt: OffsetDateTime

	// val modifiedByUser: ObjUser?
	val modifiedByUserId: Any?
	val modifiedAt: OffsetDateTime?

	val validationList: List<AggregatePartValidation>

}
