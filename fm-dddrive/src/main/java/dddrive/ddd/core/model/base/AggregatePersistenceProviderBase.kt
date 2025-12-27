package dddrive.ddd.core.model.base

import dddrive.ddd.core.model.Aggregate
import dddrive.ddd.core.model.AggregatePersistenceProvider
import dddrive.ddd.core.model.RepositoryDirectory
import dddrive.ddd.core.model.RepositoryDirectorySPI

abstract class AggregatePersistenceProviderBase<A : Aggregate>(
	intfClass: Class<A>,
) : AggregatePersistenceProvider<A> {

	val directory: RepositoryDirectory get() = RepositoryDirectory.instance

	init {
		(directory as RepositoryDirectorySPI).addPersistenceProvider(intfClass, this)
	}

}
