package io.dddrive.core.property.model

import io.dddrive.core.ddd.model.Aggregate

interface ReferenceProperty<A : Aggregate> : BaseProperty<A> {

	var id: Any?

	val idProperty: BaseProperty<Any>

	override var value: A?

}
