package dddrive.ddd.model.base

import dddrive.ddd.model.Aggregate
import dddrive.ddd.model.AggregatePersistenceProvider
import dddrive.ddd.model.RepositoryDirectory
import dddrive.ddd.model.RepositoryDirectorySPI

abstract class AggregatePersistenceProviderBase<A : Aggregate>(
	intfClass: Class<A>,
) : AggregatePersistenceProvider<A> {

	val directory: RepositoryDirectory get() = RepositoryDirectory.instance

	init {
		(directory as RepositoryDirectorySPI).addPersistenceProvider(intfClass, this)
	}

}
