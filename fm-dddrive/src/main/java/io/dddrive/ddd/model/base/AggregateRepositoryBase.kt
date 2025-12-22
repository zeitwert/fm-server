package io.dddrive.ddd.model.base

import com.github.benmanes.caffeine.cache.Caffeine
import io.dddrive.ddd.model.Aggregate
import io.dddrive.ddd.model.AggregateMeta
import io.dddrive.ddd.model.AggregatePersistenceProvider
import io.dddrive.ddd.model.AggregateRepository
import io.dddrive.ddd.model.AggregateRepositorySPI
import io.dddrive.ddd.model.AggregateSPI
import io.dddrive.ddd.model.Part
import io.dddrive.ddd.model.PartRepository
import io.dddrive.ddd.model.RepositoryDirectory
import io.dddrive.ddd.model.RepositoryDirectory.Companion.instance
import io.dddrive.ddd.model.RepositoryDirectorySPI
import io.dddrive.ddd.model.enums.CodeAggregateType
import io.dddrive.ddd.model.enums.CodeAggregateTypeEnum
import io.dddrive.ddd.model.impl.PartRepositoryImpl
import io.dddrive.property.model.EntityWithPropertiesSPI
import io.dddrive.property.model.impl.PropertyFilter
import io.dddrive.property.model.impl.PropertyHandler
import javassist.util.proxy.ProxyFactory
import java.time.OffsetDateTime
import java.util.function.Function

abstract class AggregateRepositoryBase<A : Aggregate>(
	repoIntfClass: Class<out AggregateRepository<A>>,
	private val intfClass: Class<out Aggregate>,
	private val baseClass: Class<out Aggregate>,
	private val aggregateTypeId: String,
) : AggregateRepository<A>,
	AggregateRepositorySPI<A> {

	private val aggregateProxyFactory: ProxyFactory = ProxyFactory()
	private val aggregateProxyFactoryParamTypeList: Array<Class<*>>
	private val objCache = Caffeine
		.newBuilder()
		.maximumSize(200)
		.recordStats()
		.build<Any, A>()

	private var _persistenceProvider: AggregatePersistenceProvider<A>? = null
	private var didAfterCreate = false
	private var didAfterLoad = false
	private var didBeforeStore = false
	private var didAfterStore = false

	init {
		this.aggregateProxyFactory.setSuperclass(baseClass)
		this.aggregateProxyFactory.setFilter(PropertyFilter.INSTANCE)
		this.aggregateProxyFactoryParamTypeList = arrayOf<Class<*>>(repoIntfClass, java.lang.Boolean.TYPE)
		(this.directory as RepositoryDirectorySPI).addRepository(intfClass, this)
		this.registerParts()
	}

	override val aggregateType: CodeAggregateType
		get() = CodeAggregateTypeEnum.getAggregateType(this.aggregateTypeId)

	override val directory: RepositoryDirectory
		get() = instance

	@Suppress("UNCHECKED_CAST")
	// final TODO
	override val persistenceProvider: AggregatePersistenceProvider<A>
		get() {
			if (_persistenceProvider == null) {
				_persistenceProvider = directory.getPersistenceProvider(intfClass) as AggregatePersistenceProvider<A>
			}
			return _persistenceProvider!!
		}

	override fun idToString(id: Any?): String? = if (id == null) null else this.persistenceProvider.idToString(id)

	override fun idFromString(id: String?): Any? = if (id == null) null else this.persistenceProvider.idFromString(id)

	override fun doLogChange(property: String): Boolean = !NotLoggedProperties.contains(property)

	protected fun <AA : Aggregate> addPart(
		aggregateIntfClass: Class<AA>,
		partIntfClass: Class<out Part<AA>>,
		partBaseClass: Class<out Part<AA>>,
	) {
		val partRepository: PartRepository<AA, out Part<AA>> = PartRepositoryImpl(
			aggregateIntfClass,
			partIntfClass,
			partBaseClass,
		)
		(this.directory as RepositoryDirectorySPI).addPartRepository(partIntfClass, partRepository)
	}

	override fun create(
		tenantId: Any,
		userId: Any,
		timestamp: OffsetDateTime,
	): A {
		val persistenceProvider = this.persistenceProvider
		val aggregateId = persistenceProvider.nextAggregateId()
		val aggregate = this.createAggregate(true)

		val doInitSeqNr = (aggregate as AggregateBase).doInitSeqNr
		(aggregate as EntityWithPropertiesSPI).doInit()
		check(aggregate.doInitSeqNr > doInitSeqNr) { this.getBaseClassName(aggregate) + ": doInit was propagated" }

		try {
			aggregate.disableCalc()
			val doCreateSeqNr = (aggregate as AggregateBase).doCreateSeqNr
			aggregate.doCreate(aggregateId, tenantId)
			// aggregate.setValueByPath("id", aggregateId)
			// aggregate.setValueByPath("tenantId", tenantId)
			check(aggregate.doCreateSeqNr > doCreateSeqNr) { this.getBaseClassName(aggregate) + ": doCreate was propagated" }
		} finally {
			aggregate.enableCalc()
		}

		aggregate.meta.calcAll() // TODO reconsider, currently must be here because docs are frozen in doAfterCreate

		this.didAfterCreate = false
		this.doAfterCreate(aggregate, userId, timestamp)
		// aggregate.setValueByPath("createdByUserId", userId)
		// aggregate.setValueByPath("createdAt", timestamp)
		// aggregate.fireEntityAddedChange(aggregateId)
		check(this.didAfterCreate) { this.baseClassName + ": doAfterCreate was propagated" }

		return aggregate
	}

	override fun doAfterCreate(
		aggregate: A,
		userId: Any,
		timestamp: OffsetDateTime,
	) {
		this.didAfterCreate = true
		val doAfterCreateSeqNr = (aggregate as AggregateBase).doAfterCreateSeqNr
		(aggregate as AggregateSPI).doAfterCreate(userId, timestamp)
		check(aggregate.doAfterCreateSeqNr > doAfterCreateSeqNr) { this.getBaseClassName(aggregate) + ": doAfterCreate was propagated" }
	}

	override fun get(id: Any): A = this.objCache.get(id, Function { aggrId: Any? -> this.get(aggrId!!, true) })

	override fun load(id: Any): A = this.get(id, false)

	private fun get(
		id: Any,
		isFrozen: Boolean,
	): A {
		val persistenceProvider = this.persistenceProvider
		check(persistenceProvider.isValidId(id)) { "valid id " + id + " (" + id.javaClass.getSimpleName() + ")" }

		val aggregate = this.createAggregate(false)
		val doInitSeqNr = (aggregate as AggregateBase).doInitSeqNr
		(aggregate as EntityWithPropertiesSPI).doInit()
		check(aggregate.doInitSeqNr > doInitSeqNr) { this.getBaseClassName(aggregate) + ": doInit was propagated" }

		(aggregate as AggregateMeta).beginLoad()
		persistenceProvider.doLoad(aggregate, id)
		(aggregate as AggregateMeta).endLoad()

		aggregate.meta.calcVolatile()

		if (isFrozen) {
			(aggregate as AggregateBase).freeze()
		}

		this.didAfterLoad = false
		this.doAfterLoad(aggregate)
		check(this.didAfterLoad) { this.baseClassName + ": doAfterLoad was propagated" }

		return aggregate
	}

	@Suppress("UNCHECKED_CAST")
	private fun createAggregate(isNew: Boolean): A {
		try {
			return this.aggregateProxyFactory.create(
				this.aggregateProxyFactoryParamTypeList,
				arrayOf(this, isNew),
				PropertyHandler.INSTANCE,
			) as A
		} catch (e: Throwable) {
			throw RuntimeException("Could not create aggregate $baseClassName from ${javaClass.simpleName}", e)
		}
	}

	override fun doAfterLoad(aggregate: A) {
		this.didAfterLoad = true
		val doAfterLoadSeqNr = (aggregate as AggregateBase).doAfterLoadSeqNr
		(aggregate as AggregateSPI).doAfterLoad()
		check(aggregate.doAfterLoadSeqNr > doAfterLoadSeqNr) { this.getBaseClassName(aggregate) + ": doAfterLoad was propagated" }
	}

	override fun store(
		aggregate: A,
		userId: Any,
		timestamp: OffsetDateTime,
	) {
		try {
			this.didBeforeStore = false
			this.doBeforeStore(aggregate, userId, timestamp)
			check(this.didBeforeStore) { this.baseClassName + ": doBeforeStore was propagated" }

			this.persistenceProvider.doStore(aggregate)

			this.didAfterStore = false
			this.doAfterStore(aggregate)
			check(this.didAfterStore) { this.baseClassName + ": doAfterStore was propagated" }
		} catch (e: Exception) {
			throw RuntimeException("$baseClassName: could not store aggregate (${e.message})", e)
		}
	}

	override fun doBeforeStore(
		aggregate: A,
		userId: Any,
		timestamp: OffsetDateTime,
	) {
		this.didBeforeStore = true
		val doBeforeStoreSeqNr = (aggregate as AggregateBase).doBeforeStoreSeqNr
		(aggregate as AggregateSPI).doBeforeStore(userId, timestamp)
		check(aggregate.doBeforeStoreSeqNr > doBeforeStoreSeqNr) { this.getBaseClassName(aggregate) + ": doBeforeStore was propagated" }
	}

	override fun doAfterStore(aggregate: A) {
		this.didAfterStore = true
		val doAfterStoreSeqNr = (aggregate as AggregateBase).doAfterStoreSeqNr
		(aggregate as AggregateSPI).doAfterStore()
		check(aggregate.doAfterStoreSeqNr > doAfterStoreSeqNr) { this.getBaseClassName(aggregate) + ": doAfterStore was propagated" }
		this.handleAggregateStored(aggregate.id)
	}

	override fun getAll(tenantId: Any): List<Any> = this.persistenceProvider.getAll(tenantId)

	override fun getByForeignKey(
		fkName: String,
		targetId: Any,
	): List<Any> = this.persistenceProvider.getByForeignKey(fkName, targetId)

	protected val baseClassName: String
		get() = this.baseClass.getSimpleName()

	protected fun getBaseClassName(aggregate: A?): String = aggregate!!.javaClass.getSuperclass().getSimpleName()

	fun handleAggregateStored(id: Any) {
		if (this.objCache.getIfPresent(id) != null) {
			this.objCache.invalidate(id)
		}
	}

	companion object {

		private val NotLoggedProperties = mutableSetOf<String?>(
			"id",
			"maxPartId",
			"version",
			"createdByUser",
			"createdAt",
			"modifiedByUser",
			"modifiedAt",
		)
	}

}
