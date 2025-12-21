package io.dddrive.core.property.model.impl

import io.dddrive.core.ddd.model.Aggregate
import io.dddrive.core.ddd.model.AggregateRepository
import io.dddrive.core.ddd.model.AggregateRepositorySPI
import io.dddrive.core.property.model.AggregateReferenceProperty
import io.dddrive.core.property.model.EntityWithProperties
import io.dddrive.core.property.model.base.ReferencePropertyBase

class AggregateReferencePropertyImpl<A : Aggregate>(
	entity: EntityWithProperties,
	name: String,
	val repo: AggregateRepository<A>,
	override val type: Class<A>,
) : ReferencePropertyBase<A, Any>(entity, name, Any::class.java),
	AggregateReferenceProperty<A> {

	override var value: A?
		get() = if (id == null) null else this.repo.get(id!!)
		set(value) {
			id = value?.id
		}

	override fun isValidId(id: Any?): Boolean = id == null || (this.repo as AggregateRepositorySPI<*>).persistenceProvider.isValidId(id)

}
