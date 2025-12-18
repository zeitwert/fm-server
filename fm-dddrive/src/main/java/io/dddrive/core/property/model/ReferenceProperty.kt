package io.dddrive.core.property.model

interface ReferenceProperty<T : Any, ID : Any> : BaseProperty<T> {

	val idProperty: BaseProperty<ID>

	var id: ID?

	override var value: T?

}
