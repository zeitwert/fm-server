package dddrive.property.model.impl

import dddrive.property.model.BaseProperty
import dddrive.property.model.EntityWithProperties
import dddrive.property.model.EntityWithPropertiesSPI
import dddrive.property.model.base.PropertyBase

class BasePropertyImpl<T : Any>(
	entity: EntityWithProperties,
	name: String,
	override val type: Class<T>,
	private val calculator: ((BaseProperty<T>) -> T?)? = null,
) : PropertyBase<T>(entity, name, calculator != null),
	BaseProperty<T> {

	private var storedValue: T? = null

	override var value: T?
		get() = calculator?.invoke(this) ?: storedValue
		set(value) {
			require(this.isWritable) { "writable" }
			// check that value is assignable to type
			if (value != null) {
				require(type.isAssignableFrom(value::class.java)) { "BaseProperty type mismatch: expected ${type.name}, got [$value](${value::class.java.name})" }
			}
			if (storedValue == value) {
				return
			}
			val entity = this.entity as EntityWithPropertiesSPI
			val oldValue = storedValue
			// cannot fire doBeforeSet before setting id, as path depends on id
			if (this.name != "id") {
				entity.doBeforeSet(this, value, oldValue)
			}
			storedValue = value
			fireFieldSetChange(value, oldValue)
			entity.doAfterSet(this, value, oldValue)
		}

	override fun toString(): String = "$name: $value"

}
