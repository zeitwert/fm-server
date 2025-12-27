package dddrive.ddd.core.model.impl

import dddrive.ddd.core.model.Aggregate
import dddrive.ddd.core.model.AggregateRepositorySPI
import dddrive.ddd.core.model.Part
import dddrive.ddd.core.model.PartRepository
import dddrive.ddd.core.model.PartSPI
import dddrive.ddd.core.model.base.PartBase
import dddrive.ddd.property.model.Property

class PartRepositoryImpl<A : Aggregate, P : Part<A>>(
	private val intfClass: Class<out P>,
	private val factory: (A, PartRepository<A, P>, Property<*>, Int) -> P,
) : PartRepository<A, P> {

	override fun doLogChange(property: String): Boolean = !NotLoggedProperties.contains(property)

	@Suppress("UNCHECKED_CAST")
	override fun create(
		aggregate: A,
		property: Property<*>,
		partId: Int?,
	): P {
		val isInLoad = aggregate.meta.isInLoad
		require(!isInLoad || partId != null) { "partId != null on load" }
		require(isInLoad || partId == null) { "partId == null on create" }
		val repo = aggregate.meta.repository as AggregateRepositorySPI<A>
		val id: Int =
			(
				if (isInLoad) {
					partId
				} else {
					repo.persistenceProvider.nextPartId(aggregate, this.intfClass)
				}
			)!!
		val part = this.factory(aggregate, this, property, id)
		check(isInLoad || part.meta.isNew) { "load or part.isNew" }
		check(!isInLoad || !part.meta.isNew) { "outside load or !part.isNew" }
		if (!isInLoad) {
			val doAfterCreateSeqNr = (part as PartBase<*>).doAfterCreateSeqNr
			(part as PartSPI<A>).doAfterCreate()
			check(part.doAfterCreateSeqNr > doAfterCreateSeqNr) {
				part.javaClass.simpleName + ": doAfterCreate was propagated"
			}
		}
		return part
	}

	companion object {

		private val NotLoggedProperties = mutableSetOf<String?>("id")
	}
}
