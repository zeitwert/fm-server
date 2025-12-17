package io.dddrive.core.property.model.impl

import io.dddrive.core.ddd.model.Part
import io.dddrive.core.property.model.EntityWithProperties
import io.dddrive.core.property.model.PartReferenceProperty
import io.dddrive.core.property.model.base.ReferencePropertyBase

class PartReferencePropertyImpl<P : Part<*>>(
	entity: EntityWithProperties,
	name: String,
	private val resolver: PartResolver<P>,
	override val type: Class<P>,
) : ReferencePropertyBase<P, Int>(entity, name, Int::class.java),
	PartReferenceProperty<P> {

	override var value: P?
		get() = if (this.id == null) null else this.resolver.get(this.id!!)
		set(value) {
			this.id = value?.id
		}

	override fun isValidId(id: Int?): Boolean = id == null || this.resolver.get(id) != null

}
