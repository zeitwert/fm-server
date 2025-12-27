package dddrive.ddd.property.model.impl

import dddrive.ddd.property.model.BaseProperty
import dddrive.ddd.property.model.EntityWithProperties
import dddrive.ddd.property.model.EntityWithPropertiesSPI
import dddrive.ddd.property.model.base.PropertyBase

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
			val entity = this.entity as EntityWithPropertiesSPI
			val oldValue = field
			// TODO separate handling of setting id needs to be reviewed
			if (this.name == "id") {
				// id field needs to be set before fireFieldSetChange, to get the correct path
				field = value
			} else {
				entity.doBeforeSet(this, value, oldValue)
				field = value
			}
			entity.fireFieldSetChange(this, value, oldValue)
			entity.doAfterSet(this)
		}

}
