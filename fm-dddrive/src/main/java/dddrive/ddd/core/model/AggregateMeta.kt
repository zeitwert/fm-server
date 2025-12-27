package dddrive.ddd.core.model

import dddrive.ddd.validation.model.AggregatePartValidation
import io.dddrive.oe.model.ObjUser
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
