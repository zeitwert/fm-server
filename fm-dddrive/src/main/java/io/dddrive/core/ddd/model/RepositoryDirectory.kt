package io.dddrive.core.ddd.model

import io.dddrive.core.ddd.model.impl.RepositoryDirectoryImpl
import io.dddrive.core.enums.model.Enumerated
import io.dddrive.core.enums.model.Enumeration

interface RepositoryDirectory {

	fun <A : Aggregate> getRepository(intfClass: Class<A>): AggregateRepository<A>

	fun <A : Aggregate, P : Part<A>> getPartRepository(intfClass: Class<P>): PartRepository<A, P>

	fun <A : Aggregate> getPersistenceProvider(intfClass: Class<A>): AggregatePersistenceProvider<A>

	fun <P : Part<*>> getPartPersistenceProvider(intfClass: Class<P>): PartPersistenceProvider<P>

	fun <E : Enumerated> getEnumeration(enumClass: Class<E>): Enumeration<E>

	fun getEnumeration(
		module: String,
		enumName: String,
	): Enumeration<out Enumerated>

	companion object {

		@JvmStatic
		val instance: RepositoryDirectory = RepositoryDirectoryImpl()
	}

}
