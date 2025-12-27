package dddrive.ddd.core.model

import dddrive.ddd.enums.model.Enumerated
import dddrive.ddd.enums.model.Enumeration

interface RepositoryDirectorySPI {

	fun addRepository(
		intfClass: Class<out Aggregate>,
		repo: AggregateRepository<out Aggregate>,
	)

	fun <A : Aggregate> addPartRepository(
		intfClass: Class<out Part<A>>,
		repo: PartRepository<A, out Part<A>>,
	)

	fun addPersistenceProvider(
		intfClass: Class<out Aggregate>,
		app: AggregatePersistenceProvider<out Aggregate>,
	)

	fun <P : Part<*>> addPartPersistenceProvider(
		intfClass: Class<P>,
		ppp: PartPersistenceProvider<P>,
	)

	fun <E : Enumerated> addEnumeration(
		enumClass: Class<E>,
		enumeration: Enumeration<E>,
	)

}
