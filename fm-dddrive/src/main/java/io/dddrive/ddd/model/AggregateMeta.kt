package io.dddrive.ddd.model

import io.dddrive.oe.model.ObjUser
import io.dddrive.validation.model.AggregatePartValidation
import java.time.OffsetDateTime

/**
 * A DDD Aggregate Root Meta Information.
 */
interface AggregateMeta : EntityMeta {

	val repository: AggregateRepository<*>

	val version: Int

	val validations: List<AggregatePartValidation>

	val createdAt: OffsetDateTime

	val createdByUser: ObjUser

	val modifiedAt: OffsetDateTime?

	val modifiedByUser: ObjUser?

}
