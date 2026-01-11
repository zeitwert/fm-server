package dddrive.property.model.impl

import dddrive.ddd.model.Enumerated
import dddrive.ddd.model.Enumeration
import dddrive.property.model.EntityWithProperties
import dddrive.property.model.EntityWithPropertiesSPI
import dddrive.property.model.EnumProperty
import dddrive.property.model.ReferenceProperty
import dddrive.property.model.base.ReferencePropertyBase

class EnumPropertyImpl<E : Enumerated>(
	entity: EntityWithProperties,
	name: String,
	override val type: Class<E>,
	idCalculator: ((EnumProperty<E>) -> String?)? = null,
) : ReferencePropertyBase<E, String>(
	entity,
	name,
	String::class.java,
	@Suppress("UNCHECKED_CAST")
	idCalculator as ((ReferenceProperty<E, String>) -> String?)?,
),
	EnumProperty<E> {

	override val enumeration: Enumeration<E>
		get() = (entity as EntityWithPropertiesSPI).directory.getEnumeration(type)

	override var value: E?
		get() = if (id == null) null else this.enumeration.getItem(id!!)
		set(value) {
			id = value?.id
		}

	override fun isValidId(id: String?): Boolean = id == null || this.enumeration.getItem(id) != null

}
