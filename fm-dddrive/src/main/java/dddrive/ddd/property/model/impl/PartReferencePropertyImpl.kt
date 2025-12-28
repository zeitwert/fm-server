package dddrive.ddd.property.model.impl

import dddrive.ddd.core.model.Aggregate
import dddrive.ddd.core.model.Part
import dddrive.ddd.property.model.EntityWithProperties
import dddrive.ddd.property.model.PartReferenceProperty
import dddrive.ddd.property.model.base.ReferencePropertyBase

class PartReferencePropertyImpl<A : Aggregate, P : Part<A>>(
	entity: EntityWithProperties,
	name: String,
	override val type: Class<P>,
) : ReferencePropertyBase<P, Int>(entity, name, Int::class.java),
	PartReferenceProperty<A, P> {

	@Suppress("UNCHECKED_CAST")
	override var value: P?
		get() = if (this.id == null) null else entity.getPart(this.id!!) as P
		set(value) {
			this.id = value?.id
		}

	override fun isValidId(id: Int?): Boolean = id == null || entity.getPart(id) != null

}
