package io.dddrive.ddd.model

import io.dddrive.property.model.EntityWithPropertiesSPI

/**
 * This interface defines the internal callbacks for a Part implementation.
 */
interface PartSPI<A : Aggregate> : EntityWithPropertiesSPI {

	/**
	 * Initialise a Part after creation (external, functional callback).
	 */
	fun doAfterCreate() {}

	fun delete()

}
