package io.dddrive.property.model.impl

import io.dddrive.ddd.model.Aggregate
import io.dddrive.ddd.model.AggregateRepositorySPI
import io.dddrive.property.model.AggregateReferenceProperty
import io.dddrive.property.model.EntityWithProperties
import io.dddrive.property.model.EntityWithPropertiesSPI
import io.dddrive.property.model.base.ReferencePropertyBase

class AggregateReferencePropertyImpl<A : Aggregate>(
	entity: EntityWithProperties,
	name: String,
	override val type: Class<A>,
) : ReferencePropertyBase<A, Any>(entity, name, Any::class.java),
	AggregateReferenceProperty<A> {

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
