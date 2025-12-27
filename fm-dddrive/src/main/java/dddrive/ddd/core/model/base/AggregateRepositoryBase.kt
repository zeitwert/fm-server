package dddrive.ddd.core.model.base

import com.github.benmanes.caffeine.cache.Caffeine
import dddrive.ddd.core.model.Aggregate
import dddrive.ddd.core.model.AggregatePersistenceProvider
import dddrive.ddd.core.model.AggregateRepository
import dddrive.ddd.core.model.AggregateRepositorySPI
import dddrive.ddd.core.model.AggregateSPI
import dddrive.ddd.core.model.Part
import dddrive.ddd.core.model.PartRepository
import dddrive.ddd.core.model.RepositoryDirectory
import dddrive.ddd.core.model.RepositoryDirectory.Companion.instance
import dddrive.ddd.core.model.RepositoryDirectorySPI
import dddrive.ddd.core.model.enums.CodeAggregateType
import dddrive.ddd.core.model.enums.CodeAggregateTypeEnum
import dddrive.ddd.core.model.impl.PartRepositoryImpl
import dddrive.ddd.property.model.Property
import java.time.OffsetDateTime
import java.util.function.Function

abstract class AggregateRepositoryBase<A : Aggregate>(
	private val intfClass: Class<out Aggregate>,
	private val aggregateTypeId: String,
) : AggregateRepository<A>,
	AggregateRepositorySPI<A> {

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
				_persistenceProvider =
					directory.getPersistenceProvider(intfClass) as AggregatePersistenceProvider<A>
			}
			return _persistenceProvider!!
		}

	override fun idToString(id: Any?): String? = if (id == null) null else this.persistenceProvider.idToString(id)

	override fun idFromString(id: String?): Any? = if (id == null) null else this.persistenceProvider.idFromString(id)

	override fun doLogChange(property: String): Boolean = !NotLoggedProperties.contains(property)

	protected fun <AA : Aggregate, PP : Part<AA>> addPart(
		partIntfClass: Class<out PP>,
		factory: (AA, PartRepository<AA, PP>, Property<*>, Int) -> PP,
	) {
		val partRepository: PartRepository<AA, PP> = PartRepositoryImpl(partIntfClass, factory)
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

		try {
			aggregate.meta.disableCalc()
			val doCreateSeqNr = (aggregate as AggregateBase).doCreateSeqNr
			aggregate.doCreate(aggregateId, tenantId)
			check(aggregate.doCreateSeqNr > doCreateSeqNr) {
				intfClass.simpleName + ": doCreate was propagated"
			}
		} finally {
			aggregate.meta.enableCalc()
		}

		aggregate.meta.calcAll() // TODO reconsider, currently must be here because docs are frozen in
		// doAfterCreate

		this.didAfterCreate = false
		this.doAfterCreate(aggregate, userId, timestamp)
		check(this.didAfterCreate) { intfClass.simpleName + ": doAfterCreate was propagated" }

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
		check(aggregate.doAfterCreateSeqNr > doAfterCreateSeqNr) {
			intfClass.simpleName + ": doAfterCreate was propagated"
		}
	}

	override fun get(id: Any): A = this.objCache.get(id, Function { aggrId: Any? -> this.get(aggrId!!, true) })

	override fun load(id: Any): A = this.get(id, false)

	private fun get(
		id: Any,
		isFrozen: Boolean,
	): A {
		val persistenceProvider = this.persistenceProvider
		check(persistenceProvider.isValidId(id)) {
			"valid id " + id + " (" + id.javaClass.getSimpleName() + ")"
		}

		val aggregate = this.createAggregate(false)

		(aggregate as AggregateSPI).beginLoad()
		persistenceProvider.doLoad(aggregate, id)
		aggregate.endLoad()

		aggregate.meta.calcVolatile()

		if (isFrozen) {
			(aggregate as AggregateBase).freeze()
		}

		this.didAfterLoad = false
		this.doAfterLoad(aggregate)
		check(this.didAfterLoad) { intfClass.simpleName + ": doAfterLoad was propagated" }

		return aggregate
	}

	/**
	 * Create a new aggregate instance. Concrete repositories must override this to directly
	 * instantiate their Impl class.
	 */
	protected abstract fun createAggregate(isNew: Boolean): A

	override fun doAfterLoad(aggregate: A) {
		this.didAfterLoad = true
		val doAfterLoadSeqNr = (aggregate as AggregateBase).doAfterLoadSeqNr
		(aggregate as AggregateSPI).doAfterLoad()
		check(aggregate.doAfterLoadSeqNr > doAfterLoadSeqNr) {
			intfClass.simpleName + ": doAfterLoad was propagated"
		}
	}

	override fun store(
		aggregate: A,
		userId: Any,
		timestamp: OffsetDateTime,
	) {
		try {
			this.didBeforeStore = false
			this.doBeforeStore(aggregate, userId, timestamp)
			check(this.didBeforeStore) { intfClass.simpleName + ": doBeforeStore was propagated" }

			this.persistenceProvider.doStore(aggregate)

			this.didAfterStore = false
			this.doAfterStore(aggregate)
			check(this.didAfterStore) { intfClass.simpleName + ": doAfterStore was propagated" }
		} catch (e: Exception) {
			throw RuntimeException("${intfClass.simpleName}: could not store aggregate (${e.message})", e)
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
		check(aggregate.doBeforeStoreSeqNr > doBeforeStoreSeqNr) {
			intfClass.simpleName + ": doBeforeStore was propagated"
		}
	}

	override fun doAfterStore(aggregate: A) {
		this.didAfterStore = true
		val doAfterStoreSeqNr = (aggregate as AggregateBase).doAfterStoreSeqNr
		(aggregate as AggregateSPI).doAfterStore()
		check(aggregate.doAfterStoreSeqNr > doAfterStoreSeqNr) {
			intfClass.simpleName + ": doAfterStore was propagated"
		}
		this.handleAggregateStored(aggregate.id)
	}

	override fun getByForeignKey(
		fkName: String,
		targetId: Any,
	): List<Any> = this.persistenceProvider.getByForeignKey(fkName, targetId)

	fun handleAggregateStored(id: Any) {
		if (this.objCache.getIfPresent(id) != null) {
			this.objCache.invalidate(id)
		}
	}

	companion object {

		private val NotLoggedProperties =
			setOf(
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
