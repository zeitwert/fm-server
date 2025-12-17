package io.dddrive.core.ddd.model.impl

import io.dddrive.core.ddd.model.Aggregate
import io.dddrive.core.ddd.model.AggregatePersistenceProvider
import io.dddrive.core.ddd.model.AggregateRepository
import io.dddrive.core.ddd.model.Part
import io.dddrive.core.ddd.model.PartPersistenceProvider
import io.dddrive.core.ddd.model.PartRepository
import io.dddrive.core.ddd.model.RepositoryDirectory
import io.dddrive.core.ddd.model.RepositoryDirectorySPI
import io.dddrive.core.enums.model.Enumerated
import io.dddrive.core.enums.model.Enumeration
import io.dddrive.util.Invariant

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
	): Enumeration<out Enumerated> = this.enumsById[module + "." + enumName + "Enum"]!!

	@Suppress("UNCHECKED_CAST")
	override fun <E : Enumerated> getEnumeration(enumClass: Class<E>): Enumeration<E> = this.enumsByEnumeratedClass[enumClass] as Enumeration<E>

	// 	@Override
	// 	public <E extends Enumerated> E getEnumerated(Class<E> enumClass, String itemId) {
	// 		return this.getEnumeration(enumClass).getItem(itemId);
	// 	}

	override fun <E : Enumerated> addEnumeration(
		enumClass: Class<E>,
		enumeration: Enumeration<E>,
	) {
		this.enumsById.put(enumeration.module + "." + enumeration.id, enumeration)
		this.enumsByEnumeratedClass.put(enumClass, enumeration)
	}

	@Suppress("UNCHECKED_CAST")
	override fun <A : Aggregate> getRepository(intfClass: Class<A>): AggregateRepository<A> = this.repoByIntf[intfClass] as AggregateRepository<A>

	override fun addRepository(
		intfClass: Class<out Aggregate>,
		repo: AggregateRepository<out Aggregate>,
	) {
		Invariant.assertThis(this.repoByIntf[intfClass] == null, "unique repo for class $intfClass")
		this.repoByIntf.put(intfClass, repo)
	}

	@Suppress("UNCHECKED_CAST")
	override fun <A : Aggregate, P : Part<A>> getPartRepository(intfClass: Class<P>): PartRepository<A, P> = this.partRepoByIntf[intfClass] as PartRepository<A, P>

	override fun <A : Aggregate> addPartRepository(
		intfClass: Class<out Part<A>>,
		repo: PartRepository<A, out Part<A>>,
	) {
		// assertThis(this.getPartRepository(intfClass) == null, "unique repo for class " + intfClass);
		this.partRepoByIntf.put(intfClass, repo)
	}

	@Suppress("UNCHECKED_CAST")
	override fun <A : Aggregate> getPersistenceProvider(intfClass: Class<A>): AggregatePersistenceProvider<A> = appByIntf[intfClass] as AggregatePersistenceProvider<A>

	override fun addPersistenceProvider(
		intfClass: Class<out Aggregate>,
		app: AggregatePersistenceProvider<out Aggregate>,
	) {
		Invariant.assertThis(
			this.appByIntf[intfClass] == null,
			"unique persistence provider for class $intfClass",
		)
		this.appByIntf.put(intfClass, app)
	}

	@Suppress("UNCHECKED_CAST")
	override fun <P : Part<*>> getPartPersistenceProvider(intfClass: Class<P>): PartPersistenceProvider<P> = this.pppByIntf[intfClass] as PartPersistenceProvider<P>

	override fun <P : Part<*>> addPartPersistenceProvider(
		intfClass: Class<P>,
		ppp: PartPersistenceProvider<P>,
	) {
		Invariant.assertThis(
			this.pppByIntf[intfClass] == null,
			"unique persistence provider for class $intfClass",
		)
		this.pppByIntf.put(intfClass, ppp)
	}

}
