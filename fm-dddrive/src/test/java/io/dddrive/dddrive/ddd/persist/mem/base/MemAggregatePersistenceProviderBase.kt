package io.dddrive.dddrive.ddd.persist.mem.base

import io.dddrive.core.ddd.model.Aggregate
import io.dddrive.core.ddd.model.AggregateMeta
import io.dddrive.core.ddd.model.Part
import io.dddrive.core.ddd.model.base.AggregatePersistenceProviderBase
import io.dddrive.core.oe.model.ObjTenant
import io.dddrive.core.oe.model.ObjUser
import io.dddrive.core.property.model.BaseProperty
import io.dddrive.core.property.model.ReferenceProperty
import io.dddrive.dddrive.ddd.persist.mem.pto.AggregatePto
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.time.OffsetDateTime
import java.util.concurrent.atomic.AtomicReference

abstract class MemAggregatePersistenceProviderBase<A : Aggregate, Pto : AggregatePto>(
	intfClass: Class<A>,
) : AggregatePersistenceProviderBase<A>(intfClass) {

	companion object {

		private val lastId = AtomicReference(0)
		private val lastPartId = AtomicReference(0)
	}

	protected val aggregates: MutableMap<Int, AggregatePto> = HashMap()

	override fun isValidId(id: Any): Boolean = id is Int

	override fun idToString(id: Any): String = id.toString()

	override fun idFromString(id: String): Any = id.toInt()

	override fun nextAggregateId(): Any = lastId.getAndSet(lastId.get() + 1) + 1

	override fun <P : Part<A>> nextPartId(
		aggregate: A,
		partClass: Class<P>,
	): Int = lastPartId.getAndSet(lastPartId.get() + 1) + 1

	protected fun checkVersion(
		id: Int,
		version: Int,
	): Int {
		val currentVersion = aggregates[id]?.meta?.version ?: 0
		check(version == currentVersion + 1) { "correct version" }
		return currentVersion + 1
	}

	@Suppress("UNCHECKED_CAST")
	final override fun doLoad(
		aggregate: A,
		id: Any,
	) {
		require(isValidId(id)) { "valid id" }
		val pto = aggregates[id] as? Pto
		check(pto != null) { "aggregate found for id ($id)" }

		val nonNullPto = pto

		(aggregate as AggregateMeta).disableCalc()
		try {
			this.toAggregate(nonNullPto, aggregate)
		} finally {
			(aggregate as AggregateMeta).enableCalc()
			aggregate.calcVolatile()
		}
	}

	final override fun doStore(aggregate: A) {
		val pto = this.fromAggregate(aggregate)
		this.store(pto)
	}

	@Suppress("UNCHECKED_CAST")
	protected open fun toAggregate(
		pto: Pto,
		aggregate: A,
	) {
		(aggregate.getProperty("id") as? BaseProperty<Any?>)?.value = pto.id
		(aggregate.getProperty("version") as? BaseProperty<Int?>)?.value = pto.meta?.version
		(aggregate.getProperty("maxPartId") as? BaseProperty<Int?>)?.value = pto.meta?.maxPartId
		(aggregate.getProperty("tenant") as? ReferenceProperty<ObjTenant>)?.id = pto.tenantId
		(aggregate.getProperty("owner") as? ReferenceProperty<ObjUser>)?.id = pto.meta?.ownerId
		(aggregate.getProperty("caption") as? BaseProperty<String?>)?.value = pto.caption
		(aggregate.getProperty("createdByUser") as? ReferenceProperty<ObjUser>)?.id = pto.meta?.createdByUserId
		(aggregate.getProperty("createdAt") as? BaseProperty<OffsetDateTime?>)?.value = pto.meta?.createdAt
		(aggregate.getProperty("modifiedByUser") as? ReferenceProperty<ObjUser>)?.id = pto.meta?.modifiedByUserId
		(aggregate.getProperty("modifiedAt") as? BaseProperty<OffsetDateTime?>)?.value = pto.meta?.modifiedAt
	}

	protected abstract fun fromAggregate(aggregate: A): Pto

	protected open fun store(pto: Pto) {
		pto.id?.let { aggregates[it] = pto }
	}

	override fun getAll(tenantId: Any): List<Any> =
		this.aggregates.values
			.mapNotNull { pto -> pto.id }
			.toList()

	override fun getByForeignKey(
		fkName: String,
		targetId: Any,
	): List<Any> {
		val getterName = "get${fkName.substring(0, 1).uppercase()}${fkName.substring(1)}"
		return try {
			val m: Method = this.getDtoClass().getMethod(getterName)
			this.aggregates.values
				.filter { pto ->
					try {
						targetId == m.invoke(pto)
					} catch (e: IllegalAccessException) {
						throw RuntimeException(e)
					} catch (e: InvocationTargetException) {
						throw RuntimeException(e)
					}
				}.mapNotNull { pto -> pto.id }
				.toList()
		} catch (e: NoSuchMethodException) {
			throw RuntimeException(e)
		}
	}

	@Suppress("UNCHECKED_CAST")
	protected fun getDtoClass(): Class<Pto> = (this::class.java.genericSuperclass as ParameterizedType).actualTypeArguments[1] as Class<Pto>
}
