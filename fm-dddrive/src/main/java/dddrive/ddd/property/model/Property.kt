package dddrive.ddd.property.model

interface Property<T : Any> {

	val entity: EntityWithProperties

	val relativePath: String

	val path: String

	val name: String

	val isWritable: Boolean

}
