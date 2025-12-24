package io.dddrive.dddrive.ddd.persist.mem.base

import io.dddrive.ddd.model.Aggregate
import io.dddrive.ddd.model.AggregateMeta
import io.dddrive.ddd.model.Part
import io.dddrive.ddd.model.base.AggregatePersistenceProviderBase
import io.dddrive.dddrive.ddd.persist.mem.pto.AggregatePto
import io.dddrive.path.setValueByPath
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
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
		aggregate.setValueByPath("id", pto.id)
		aggregate.setValueByPath("version", pto.meta?.version)
		aggregate.setValueByPath("maxPartId", pto.meta?.maxPartId)
		aggregate.setValueByPath("tenantId", pto.tenantId)
		aggregate.setValueByPath("ownerId", pto.meta?.ownerId)
		aggregate.setValueByPath("caption", pto.caption)
		aggregate.setValueByPath("createdByUserId", pto.meta?.createdByUserId)
		aggregate.setValueByPath("createdAt", pto.meta?.createdAt)
		aggregate.setValueByPath("modifiedByUserId", pto.meta?.modifiedByUserId)
		aggregate.setValueByPath("modifiedAt", pto.meta?.modifiedAt)
	}

	protected abstract fun fromAggregate(aggregate: A): Pto

	protected open fun store(pto: Pto) {
		pto.id?.let { aggregates[it] = pto }
	}

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
