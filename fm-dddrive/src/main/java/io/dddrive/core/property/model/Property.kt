package io.dddrive.core.property.model

interface Property<T : Any> {

	val entity: EntityWithProperties

	val relativePath: String

	val path: String

	val name: String

	val isWritable: Boolean

}
