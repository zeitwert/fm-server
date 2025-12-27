package dddrive.ddd.core.model

import dddrive.ddd.property.model.EntityWithProperties

/**
 * A Part is an Entity that belongs to an Aggregate (but might be attached to another part as parent).
 */
interface Part<A : Aggregate> : EntityWithProperties {

	val id: Int

	val meta: PartMeta<A>

}
