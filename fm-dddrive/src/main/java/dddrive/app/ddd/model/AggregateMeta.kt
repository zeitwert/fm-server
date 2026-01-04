package dddrive.app.ddd.model

import dddrive.app.validation.model.AggregatePartValidation
import dddrive.ddd.core.model.AggregateMeta
import java.time.OffsetDateTime

interface AggregateMeta : AggregateMeta {

	val createdByUserId: Any
	val createdAt: OffsetDateTime

	val modifiedByUserId: Any?
	val modifiedAt: OffsetDateTime?

	val validationList: List<AggregatePartValidation>

}
