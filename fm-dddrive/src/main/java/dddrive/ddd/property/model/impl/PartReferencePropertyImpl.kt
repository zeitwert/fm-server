package dddrive.ddd.property.model.impl

import dddrive.ddd.core.model.Aggregate
import dddrive.ddd.core.model.Part
import dddrive.ddd.property.model.EntityWithProperties
import dddrive.ddd.property.model.EntityWithPropertiesSPI
import dddrive.ddd.property.model.PartReferenceProperty
import dddrive.ddd.property.model.ReferenceProperty
import dddrive.ddd.property.model.base.ReferencePropertyBase

class PartReferencePropertyImpl<A : Aggregate, P : Part<A>>(
	entity: EntityWithProperties,
	name: String,
	override val type: Class<P>,
	idCalculator: ((PartReferenceProperty<A, P>) -> Int?)? = null,
) : ReferencePropertyBase<P, Int>(
	entity,
	name,
	Int::class.java,
	@Suppress("UNCHECKED_CAST")
	idCalculator as ((ReferenceProperty<P, Int>) -> Int?)?,
),
	PartReferenceProperty<A, P> {

	@Suppress("UNCHECKED_CAST")
	override var value: P?
		get() = if (this.id == null) null else (entity as EntityWithPropertiesSPI).getPart(this.id!!) as P
		set(value) {
			this.id = value?.id
		}

	override fun isValidId(id: Int?): Boolean = id == null || (entity as EntityWithPropertiesSPI).getPart(id) != null

}
