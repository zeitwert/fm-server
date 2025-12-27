package dddrive.ddd.core.model

import dddrive.ddd.property.model.EntityWithProperties

/**
 * A DDD Aggregate Root.
 */
interface Aggregate : EntityWithProperties {

	val id: Any

	val meta: AggregateMeta

}
