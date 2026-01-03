package dddrive.ddd.property.model

import dddrive.ddd.core.model.Aggregate

interface AggregateReferenceProperty<A : Aggregate> : ReferenceProperty<A, Any> {

	/**
	 * The class of the referenced aggregate type.
	 */
	val targetClass: Class<A>

}
