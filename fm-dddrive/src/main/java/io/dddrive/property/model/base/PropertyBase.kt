package io.dddrive.property.model.base

import io.dddrive.property.model.EntityWithProperties
import io.dddrive.property.model.EntityWithPropertiesSPI
import io.dddrive.property.model.Property

abstract class PropertyBase<T : Any>(
	override val entity: EntityWithProperties,
	override val name: String,
) : Property<T> {

	override val relativePath: String
		get() {
			val relativePath = (this.entity as EntityWithPropertiesSPI).relativePath
			return if (relativePath.isEmpty()) this.name else relativePath + "." + this.name
		}

	override val path: String
		get() = (this.entity as EntityWithPropertiesSPI).path + "." + this.name

	override val isWritable: Boolean
		get() = !this.entity.isFrozen

}
