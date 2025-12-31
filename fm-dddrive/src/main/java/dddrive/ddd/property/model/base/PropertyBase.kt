package dddrive.ddd.property.model.base

import dddrive.ddd.core.model.Entity
import dddrive.ddd.path.path
import dddrive.ddd.property.model.EntityWithProperties
import dddrive.ddd.property.model.EntityWithPropertiesSPI
import dddrive.ddd.property.model.Property

abstract class PropertyBase<T : Any>(
	final override val entity: EntityWithProperties,
	override val name: String,
) : Property<T> {

	override val isWritable: Boolean
		get() = !this.entity.isFrozen

	fun firePartAddedChange(part: EntityWithPropertiesSPI) {
		if (!entity.isInLoad && (entity as EntityWithPropertiesSPI).doLogChange(this)) {
			val path = (part as Entity<*>).path()
			entity.fireFieldChange("add", path, part.id, null, entity.isInCalc)
		}
	}

	fun firePartRemovedChange(part: EntityWithPropertiesSPI) {
		if (!entity.isInLoad && (entity as EntityWithPropertiesSPI).doLogChange(this)) {
			val path = (part as Entity<*>).path()
			entity.fireFieldChange("remove", path, null, part.id, entity.isInCalc)
		}
	}

	fun fireValueAddedChange(
		value: Any,
	) {
		if (!entity.isInLoad && (entity as EntityWithPropertiesSPI).doLogChange(this)) {
			entity.fireFieldChange("add", path(), value, null, entity.isInCalc)
		}
	}

	fun fireValueRemovedChange(
		value: Any,
	) {
		if (!entity.isInLoad && (entity as EntityWithPropertiesSPI).doLogChange(this)) {
			entity.fireFieldChange("remove", path(), value, null, entity.isInCalc)
		}
	}

	fun fireFieldSetChange(
		value: Any?,
		oldValue: Any?,
	) {
		if (!entity.isInLoad && (entity as EntityWithPropertiesSPI).doLogChange(this)) {
			val op = if (oldValue == null) "add" else "replace"
			entity.fireFieldChange(op, path(), value, oldValue, entity.isInCalc)
		}
	}

}
