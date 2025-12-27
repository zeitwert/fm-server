package dddrive.ddd.core.model.base

import dddrive.ddd.core.model.Part
import dddrive.ddd.core.model.PartPersistenceProvider
import dddrive.ddd.core.model.RepositoryDirectory
import dddrive.ddd.core.model.RepositoryDirectorySPI

abstract class PartPersistenceProviderBase<P : Part<*>>(
	intfClass: Class<P>,
) : PartPersistenceProvider<P> {

	val directory: RepositoryDirectory get() = RepositoryDirectory.instance

	init {
		(directory as RepositoryDirectorySPI).addPartPersistenceProvider(intfClass, this)
	}

}
