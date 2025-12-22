package io.dddrive.ddd.model.base

import io.dddrive.ddd.model.Aggregate
import io.dddrive.ddd.model.AggregatePersistenceProvider
import io.dddrive.ddd.model.RepositoryDirectory
import io.dddrive.ddd.model.RepositoryDirectorySPI

abstract class AggregatePersistenceProviderBase<A : Aggregate>(
	intfClass: Class<A>,
) : AggregatePersistenceProvider<A> {

	val directory: RepositoryDirectory get() = RepositoryDirectory.instance

	init {
		(directory as RepositoryDirectorySPI).addPersistenceProvider(intfClass, this)
	}

}
