package dddrive.ddd.property.model.impl

import dddrive.ddd.core.model.Aggregate
import dddrive.ddd.core.model.AggregateRepositorySPI
import dddrive.ddd.property.model.AggregateReferenceProperty
import dddrive.ddd.property.model.EntityWithProperties
import dddrive.ddd.property.model.EntityWithPropertiesSPI
import dddrive.ddd.property.model.base.ReferencePropertyBase

class AggregateReferencePropertyImpl<A : Aggregate>(
	entity: EntityWithProperties,
	name: String,
	override val type: Class<A>,
) : ReferencePropertyBase<A, Any>(entity, name, Any::class.java),
	AggregateReferenceProperty<A> {

	override val targetClass: Class<A> get() = type

	override var value: A?
		get() {
			val repo = (entity as EntityWithPropertiesSPI).directory.getRepository(type)
			return if (id == null) null else repo.get(id!!)
		}
		set(value) {
			id = value?.id
		}

	override fun isValidId(id: Any?): Boolean {
		val repo = (entity as EntityWithPropertiesSPI).directory.getRepository(type)
		return id == null || (repo as AggregateRepositorySPI<*>).persistenceProvider.isValidId(id)
	}

}
