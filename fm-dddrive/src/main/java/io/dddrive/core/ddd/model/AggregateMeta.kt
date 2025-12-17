package io.dddrive.core.ddd.model

import io.dddrive.core.oe.model.ObjUser
import io.dddrive.core.property.model.PropertyChangeListener
import io.dddrive.core.validation.model.AggregatePartValidation
import java.time.OffsetDateTime

/**
 * A DDD Aggregate Root Meta Information.
 */
interface AggregateMeta : EntityMeta {

	val repository: AggregateRepository<*>

	fun beginLoad()

	fun endLoad()

	fun addPropertyChangeListener(listener: PropertyChangeListener)

	fun removePropertyChangeListener(listener: PropertyChangeListener)

	val version: Int

	val validations: List<AggregatePartValidation>

	val createdAt: OffsetDateTime

	val createdByUser: ObjUser

	val modifiedAt: OffsetDateTime?

	val modifiedByUser: ObjUser?

}
