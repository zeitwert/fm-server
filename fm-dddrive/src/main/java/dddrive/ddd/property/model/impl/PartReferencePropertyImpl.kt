package dddrive.ddd.property.model.impl

import dddrive.ddd.core.model.Part
import dddrive.ddd.property.model.EntityWithProperties
import dddrive.ddd.property.model.PartReferenceProperty
import dddrive.ddd.property.model.base.ReferencePropertyBase

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
