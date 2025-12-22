package io.dddrive.ddd.model.impl

import io.dddrive.ddd.model.Aggregate
import io.dddrive.ddd.model.AggregateRepositorySPI
import io.dddrive.ddd.model.Part
import io.dddrive.ddd.model.PartRepository
import io.dddrive.ddd.model.PartSPI
import io.dddrive.property.model.Property
import io.dddrive.property.model.impl.PropertyFilter
import io.dddrive.property.model.impl.PropertyHandler
import javassist.util.proxy.ProxyFactory

class PartRepositoryImpl<A : Aggregate, P : Part<A>>(
	aggregateIntfClass: Class<out A>,
	private val intfClass: Class<out P>,
	private val baseClass: Class<out P>,
) : PartRepository<A, P> {

	private val partProxyFactory: ProxyFactory = ProxyFactory()
	private val partProxyFactoryParamTypeList: Array<Class<*>>

	init {
		this.partProxyFactory.setSuperclass(baseClass)
		this.partProxyFactory.setFilter(PropertyFilter.INSTANCE)
		this.partProxyFactoryParamTypeList =
			arrayOf<Class<*>>(aggregateIntfClass, PartRepository::class.java, Property::class.java, Int::class.java)
	}

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
		val id: Int = (if (isInLoad) partId else repo.persistenceProvider.nextPartId(aggregate, this.intfClass))!!
		try {
			val part = this.partProxyFactory.create(
				this.partProxyFactoryParamTypeList,
				arrayOf<Any?>(aggregate, this, property, id),
				PropertyHandler.INSTANCE,
			) as P
			check(isInLoad || part.meta.isNew) { "load or part.isNew" }
			check(!isInLoad || !part.meta.isNew) { "outside load or !part.isNew" }
			if (!isInLoad && part is PartSPI<*>) {
				(part as PartSPI<A?>).doAfterCreate()
			}
			return part
		} catch (e: ReflectiveOperationException) {
			throw RuntimeException(
				"Could not create part " + this.baseClass.getSimpleName(),
				e,
			) // Adjusted error message
		} catch (e: RuntimeException) {
			throw RuntimeException("Could not create part " + this.baseClass.getSimpleName(), e)
		}
	}

	companion object {

		private val NotLoggedProperties = mutableSetOf<String?>("id")
	}

}
