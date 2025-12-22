package io.dddrive.ddd.model.base

import io.dddrive.ddd.model.Part
import io.dddrive.ddd.model.PartPersistenceProvider
import io.dddrive.ddd.model.RepositoryDirectory
import io.dddrive.ddd.model.RepositoryDirectorySPI

abstract class PartPersistenceProviderBase<P : Part<*>>(
	intfClass: Class<P>,
) : PartPersistenceProvider<P> {

	val directory: RepositoryDirectory get() = RepositoryDirectory.instance

	init {
		(directory as RepositoryDirectorySPI).addPartPersistenceProvider(intfClass, this)
	}

}
