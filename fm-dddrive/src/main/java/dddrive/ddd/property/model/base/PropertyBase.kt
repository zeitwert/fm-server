package dddrive.ddd.property.model.base

import dddrive.ddd.property.model.EntityWithProperties
import dddrive.ddd.property.model.Property

abstract class PropertyBase<T : Any>(
	override val entity: EntityWithProperties,
	override val name: String,
) : Property<T> {

	override val isWritable: Boolean
		get() = !this.entity.isFrozen

}
