package dddrive.ddd.model.base

import com.github.benmanes.caffeine.cache.Caffeine
import dddrive.ddd.model.Aggregate
import dddrive.ddd.model.AggregatePersistenceProvider
import dddrive.ddd.model.AggregateRepository
import dddrive.ddd.model.AggregateRepositorySPI
import dddrive.ddd.model.AggregateSPI
import dddrive.ddd.model.Part
import dddrive.ddd.model.PartRepository
import dddrive.ddd.model.RepositoryDirectory
import dddrive.ddd.model.RepositoryDirectory.Companion.instance
import dddrive.ddd.model.RepositoryDirectorySPI
import dddrive.ddd.model.enums.CodeAggregateType
import dddrive.ddd.model.enums.CodeAggregateTypeEnum
import dddrive.ddd.model.impl.PartRepositoryImpl
import dddrive.property.model.Property
import dddrive.property.path.setValueByPath
import java.util.function.Function

abstract class AggregateRepositoryBase<A : Aggregate>(
	override val intfClass: Class<out Aggregate>,
	private val aggregateTypeId: String,
) : AggregateRepository<A>,
	AggregateRepositorySPI<A> {

	private val objCache = Caffeine
		.newBuilder()
		.maximumSize(200)
		.recordStats()
		.build<Any, A>()

	private var didAfterCreate = false
	private var didAfterLoad = false
	private var didBeforeStore = false
	private var didAfterStore = false

	init {
		(directory as RepositoryDirectorySPI).addRepository(intfClass, this)
		registerParts()
	}

	override val aggregateType: CodeAggregateType
		get() = CodeAggregateTypeEnum.getAggregateType(aggregateTypeId)

	override val directory: RepositoryDirectory
		get() = instance

	@Suppress("UNCHECKED_CAST")
	override val persistenceProvider: AggregatePersistenceProvider<A>
		get() = directory.getPersistenceProvider(intfClass) as AggregatePersistenceProvider<A>

	override fun idToString(id: Any?): String? = if (id == null) null else persistenceProvider.idToString(id)

	override fun idFromString(id: String?): Any? = if (id == null) null else persistenceProvider.idFromString(id)

	override fun doLogChange(property: String): Boolean = !NotLoggedProperties.contains(property)

	protected fun <AA : Aggregate, PP : Part<AA>> addPart(
		partIntfClass: Class<out PP>,
		factory: (AA, PartRepository<AA, PP>, Property<*>, Int) -> PP,
	) {
		val partRepository: PartRepository<AA, PP> = PartRepositoryImpl(partIntfClass, factory)
		(directory as RepositoryDirectorySPI).addPartRepository(partIntfClass, partRepository)
	}

	override fun create(): A {
		val aggregateId = persistenceProvider.nextAggregateId()
		val aggregate = createAggregate(true)
		aggregate.setValueByPath("id", aggregateId)

		didAfterCreate = false
		doAfterCreate(aggregate)
		check(didAfterCreate) { intfClass.simpleName + ": doAfterCreate was propagated" }

		aggregate.meta.calcAll()

		return aggregate
	}

	override fun doAfterCreate(aggregate: A) {
		didAfterCreate = true
		val doAfterCreateSeqNr = (aggregate as AggregateBase).doAfterCreateSeqNr
		(aggregate as AggregateSPI).doAfterCreate()
		check(aggregate.doAfterCreateSeqNr > doAfterCreateSeqNr) { intfClass.simpleName + ": doAfterCreate was propagated" }
	}

	override fun get(id: Any): A = objCache.get(id, Function { aggrId: Any? -> get(aggrId!!, true) })

	override fun load(id: Any): A = get(id, false)

	private fun get(
		id: Any,
		isFrozen: Boolean,
	): A {
		check(persistenceProvider.isValidId(id)) { "valid id $id (${id.javaClass.getSimpleName()})" }

		val aggregate = createAggregate(false)

		(aggregate as AggregateSPI).beginLoad()
		persistenceProvider.doLoad(aggregate, id)
		aggregate.endLoad()

		aggregate.meta.calcVolatile()

		if (isFrozen) {
			(aggregate as AggregateBase).freeze()
		}

		didAfterLoad = false
		doAfterLoad(aggregate)
		check(didAfterLoad) { intfClass.simpleName + ": doAfterLoad was propagated" }

		return aggregate
	}

	override fun doAfterLoad(aggregate: A) {
		didAfterLoad = true
		val doAfterLoadSeqNr = (aggregate as AggregateBase).doAfterLoadSeqNr
		(aggregate as AggregateSPI).doAfterLoad()
		check(aggregate.doAfterLoadSeqNr > doAfterLoadSeqNr) { intfClass.simpleName + ": doAfterLoad was propagated" }
	}

	override fun store(aggregate: A) {
		try {
			didBeforeStore = false
			doBeforeStore(aggregate)
			check(didBeforeStore) { intfClass.simpleName + ": doBeforeStore was propagated" }

			persistenceProvider.doStore(aggregate)

			didAfterStore = false
			doAfterStore(aggregate)
			check(didAfterStore) { intfClass.simpleName + ": doAfterStore was propagated" }
		} catch (e: Exception) {
			throw RuntimeException("${intfClass.simpleName}: could not store aggregate (${e.message})", e)
		}
	}

	override fun doBeforeStore(aggregate: A) {
		didBeforeStore = true
		val doBeforeStoreSeqNr = (aggregate as AggregateBase).doBeforeStoreSeqNr
		(aggregate as AggregateSPI).doBeforeStore()
		check(aggregate.doBeforeStoreSeqNr > doBeforeStoreSeqNr) { intfClass.simpleName + ": doBeforeStore was propagated" }
	}

	override fun doAfterStore(aggregate: A) {
		didAfterStore = true
		val doAfterStoreSeqNr = (aggregate as AggregateBase).doAfterStoreSeqNr
		(aggregate as AggregateSPI).doAfterStore()
		check(aggregate.doAfterStoreSeqNr > doAfterStoreSeqNr) { intfClass.simpleName + ": doAfterStore was propagated" }
		handleAggregateStored(aggregate.id)
	}

	fun handleAggregateStored(id: Any) {
		if (objCache.getIfPresent(id) != null) {
			objCache.invalidate(id)
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
