package io.dddrive.property.model.impl

import io.dddrive.enums.model.Enumerated
import io.dddrive.enums.model.Enumeration
import io.dddrive.property.model.EntityWithProperties
import io.dddrive.property.model.EnumProperty
import io.dddrive.property.model.base.ReferencePropertyBase

class EnumPropertyImpl<E : Enumerated>(
	entity: EntityWithProperties,
	name: String,
	override val enumeration: Enumeration<E>,
	override val type: Class<E>,
) : ReferencePropertyBase<E, String>(entity, name, String::class.java),
	EnumProperty<E> {

	override var value: E?
		get() = if (id == null) null else this.enumeration.getItem(id!!)
		set(value) {
			id = value?.id
		}

	override fun isValidId(id: String?): Boolean = id == null || this.enumeration.getItem(id) != null

}
