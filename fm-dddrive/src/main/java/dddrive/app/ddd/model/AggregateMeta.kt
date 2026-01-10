package dddrive.app.ddd.model

import dddrive.ddd.model.AggregateMeta
import java.time.OffsetDateTime

interface AggregateMeta : AggregateMeta {

	val createdByUserId: Any
	val createdAt: OffsetDateTime

	val modifiedByUserId: Any?
	val modifiedAt: OffsetDateTime?

	val validationList: List<AggregatePartValidation>

}
