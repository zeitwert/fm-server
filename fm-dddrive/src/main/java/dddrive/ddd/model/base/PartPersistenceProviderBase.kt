package dddrive.ddd.model.base

import dddrive.ddd.model.Part
import dddrive.ddd.model.PartPersistenceProvider
import dddrive.ddd.model.RepositoryDirectory
import dddrive.ddd.model.RepositoryDirectorySPI

abstract class PartPersistenceProviderBase<P : Part<*>>(
	intfClass: Class<P>,
) : PartPersistenceProvider<P> {

	val directory: RepositoryDirectory get() = RepositoryDirectory.instance

	init {
		(directory as RepositoryDirectorySPI).addPartPersistenceProvider(intfClass, this)
	}

}
