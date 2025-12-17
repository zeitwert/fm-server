package io.dddrive.core.ddd.model

import io.dddrive.core.property.model.EntityWithProperties

/**
 * A Part is an Entity that belongs to an Aggregate (but might be attached to another part as parent).
 */
interface Part<A : Aggregate> : EntityWithProperties {

	val aggregate: A

	val id: Int

	val meta: PartMeta<A>

}
