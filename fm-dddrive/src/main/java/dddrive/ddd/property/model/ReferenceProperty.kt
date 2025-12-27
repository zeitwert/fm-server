package dddrive.ddd.property.model

interface ReferenceProperty<T : Any, ID : Any> : BaseProperty<T> {

	val idProperty: BaseProperty<ID>

	var id: ID?

	override var value: T?

}
