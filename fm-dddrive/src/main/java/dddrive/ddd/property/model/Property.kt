package dddrive.ddd.property.model

interface Property<T : Any> {

	val entity: EntityWithProperties

	val name: String

	val isWritable: Boolean

}
