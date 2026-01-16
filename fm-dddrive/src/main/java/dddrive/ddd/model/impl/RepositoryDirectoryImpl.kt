package dddrive.ddd.model.impl

import dddrive.ddd.model.Aggregate
import dddrive.ddd.model.AggregatePersistenceProvider
import dddrive.ddd.model.AggregateRepository
import dddrive.ddd.model.Enumerated
import dddrive.ddd.model.Enumeration
import dddrive.ddd.model.Part
import dddrive.ddd.model.PartPersistenceProvider
import dddrive.ddd.model.PartRepository
import dddrive.ddd.model.RepositoryDirectory
import dddrive.ddd.model.RepositoryDirectorySPI

class RepositoryDirectoryImpl :
	RepositoryDirectory,
	RepositoryDirectorySPI {

	private val repoByIntf: MutableMap<Class<out Aggregate>, AggregateRepository<*>> = mutableMapOf()
	private val partRepoByIntf: MutableMap<Class<out Part<*>>, PartRepository<*, *>> = mutableMapOf()

	private val appByIntf: MutableMap<Class<out Aggregate>, AggregatePersistenceProvider<*>> = mutableMapOf()
	private val pppByIntf: MutableMap<Class<out Part<*>>, PartPersistenceProvider<*>> = mutableMapOf()

	private val enumsById: MutableMap<String, Enumeration<out Enumerated>> = mutableMapOf()
	private val enumsByEnumeratedClass: MutableMap<Class<out Enumerated>, Enumeration<out Enumerated>> = mutableMapOf()

	override fun getEnumeration(
		module: String,
		enumName: String,
	): Enumeration<out Enumerated> =
		try {
			enumsById[module + "." + enumName + "Enum"]!!
		} catch (e: Exception) {
			throw IllegalArgumentException("Enumeration not found: $module.$enumName", e)
		}

	@Suppress("UNCHECKED_CAST")
	override fun <E : Enumerated> getEnumeration(enumClass: Class<E>): Enumeration<E> =
		try {
			enumsByEnumeratedClass[enumClass] as Enumeration<E>
		} catch (e: Exception) {
			throw IllegalArgumentException("Enumeration for class not found: $enumClass", e)
		}

	// 	@Override
	// 	public <E extends Enumerated> E getEnumerated(Class<E> enumClass, String itemId) {
	// 		return getEnumeration(enumClass).getItem(itemId);
	// 	}

	override fun <E : Enumerated> addEnumeration(
		enumClass: Class<E>,
		enumeration: Enumeration<E>,
	) {
		enumsById.put(enumeration.module + "." + enumeration.id, enumeration)
		enumsByEnumeratedClass.put(enumClass, enumeration)
	}

	@Suppress("UNCHECKED_CAST")
	override fun <A : Aggregate> getRepository(intfClass: Class<A>): AggregateRepository<A> =
		try {
			repoByIntf[intfClass] as AggregateRepository<A>
		} catch (e: Exception) {
			throw IllegalArgumentException("Repository not found for class: $intfClass", e)
		}

	override fun addRepository(
		intfClass: Class<out Aggregate>,
		repo: AggregateRepository<out Aggregate>,
	) {
		require(repoByIntf[intfClass] == null) { "unique repo for class $intfClass" }
		repoByIntf.put(intfClass, repo)
	}

	@Suppress("UNCHECKED_CAST")
	override fun <A : Aggregate, P : Part<A>> getPartRepository(intfClass: Class<P>): PartRepository<A, P> =
		try {
			partRepoByIntf[intfClass] as PartRepository<A, P>
		} catch (e: Exception) {
			throw IllegalArgumentException("Part repository not found for class: $intfClass", e)
		}

	override fun <A : Aggregate> addPartRepository(
		intfClass: Class<out Part<A>>,
		repo: PartRepository<A, out Part<A>>,
	) {
		// assertThis(getPartRepository(intfClass) == null, "unique repo for class " + intfClass);
		partRepoByIntf.put(intfClass, repo)
	}

	@Suppress("UNCHECKED_CAST")
	override fun <A : Aggregate> getPersistenceProvider(intfClass: Class<A>): AggregatePersistenceProvider<A> =
		try {
			appByIntf[intfClass] as AggregatePersistenceProvider<A>
		} catch (e: Exception) {
			throw IllegalArgumentException("Persistence provider not found for class: $intfClass", e)
		}

	override fun addPersistenceProvider(
		intfClass: Class<out Aggregate>,
		app: AggregatePersistenceProvider<out Aggregate>,
	) {
		require(appByIntf[intfClass] == null) { "unique persistence provider for class $intfClass" }
		appByIntf.put(intfClass, app)
	}

	@Suppress("UNCHECKED_CAST")
	override fun <P : Part<*>> getPartPersistenceProvider(intfClass: Class<P>): PartPersistenceProvider<P> =
		try {
			pppByIntf[intfClass] as PartPersistenceProvider<P>
		} catch (e: Exception) {
			throw IllegalArgumentException("Part persistence provider not found for class: $intfClass", e)
		}

	override fun <P : Part<*>> addPartPersistenceProvider(
		intfClass: Class<P>,
		ppp: PartPersistenceProvider<P>,
	) {
		require(pppByIntf[intfClass] == null) { "unique persistence provider for class $intfClass" }
		pppByIntf.put(intfClass, ppp)
	}

	/**
	 * Clear all providers and repositories, but preserve enumerations.
	 * This is used when resetting for a new test context while keeping
	 * enum items that were registered during class loading.
	 */
	fun clearProvidersAndRepositories() {
		repoByIntf.clear()
		partRepoByIntf.clear()
		appByIntf.clear()
		pppByIntf.clear()
	}

}
