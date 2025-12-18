package io.dddrive.core.property.model.impl

import io.dddrive.core.property.model.BaseProperty
import io.dddrive.core.property.model.EntityWithProperties
import io.dddrive.core.property.model.EntityWithPropertiesSPI
import io.dddrive.core.property.model.base.PropertyBase

class BasePropertyImpl<T : Any>(
	entity: EntityWithProperties,
	name: String,
	override val type: Class<T>,
) : PropertyBase<T>(entity, name),
	BaseProperty<T> {

	override var value: T? = null
		set(value) {
			require(this.isWritable) { "writable" }
			if (field == value) {
				return
			}
			val oldValue = field
			val entity: EntityWithPropertiesSPI?
			// TODO separate handling of setting id needs to be reviewed
			if (this.name == "id") {
				// id field needs to be set before fireFieldSetChange, to get the correct path
				field = value
				entity = this.entity as EntityWithPropertiesSPI
			} else {
				entity = this.entity as EntityWithPropertiesSPI
				entity.doBeforeSet(this, value, oldValue)
				field = value
			}
			entity.fireFieldSetChange(this, value, oldValue)
			entity.doAfterSet(this)
		}

}
