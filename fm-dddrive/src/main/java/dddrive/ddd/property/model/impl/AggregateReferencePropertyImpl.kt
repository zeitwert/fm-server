package dddrive.ddd.property.model.impl

import dddrive.ddd.model.Aggregate
import dddrive.ddd.model.AggregateRepositorySPI
import dddrive.ddd.property.model.AggregateReferenceProperty
import dddrive.ddd.property.model.EntityWithProperties
import dddrive.ddd.property.model.EntityWithPropertiesSPI
import dddrive.ddd.property.model.ReferenceProperty
import dddrive.ddd.property.model.base.ReferencePropertyBase

class AggregateReferencePropertyImpl<A : Aggregate>(
	entity: EntityWithProperties,
	name: String,
	override val type: Class<A>,
	idCalculator: ((AggregateReferenceProperty<A>) -> Any?)? = null,
) : ReferencePropertyBase<A, Any>(
		entity,
		name,
		Any::class.java,
		@Suppress("UNCHECKED_CAST")
		idCalculator as ((ReferenceProperty<A, Any>) -> Any?)?,
	),
	AggregateReferenceProperty<A> {

	override val aggregateType: Class<A> get() = type

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
