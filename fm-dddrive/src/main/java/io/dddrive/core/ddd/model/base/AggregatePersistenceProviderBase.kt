package io.dddrive.core.ddd.model.base

import io.dddrive.core.ddd.model.Aggregate
import io.dddrive.core.ddd.model.AggregatePersistenceProvider
import io.dddrive.core.ddd.model.RepositoryDirectory
import io.dddrive.core.ddd.model.RepositoryDirectorySPI

abstract class AggregatePersistenceProviderBase<A : Aggregate>(
	intfClass: Class<A>,
) : AggregatePersistenceProvider<A> {

	val directory: RepositoryDirectory get() = RepositoryDirectory.getInstance()

	init {
		(directory as RepositoryDirectorySPI).addPersistenceProvider(intfClass, this)
	}

}
