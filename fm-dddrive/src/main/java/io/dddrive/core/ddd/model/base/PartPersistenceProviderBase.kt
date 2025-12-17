package io.dddrive.core.ddd.model.base

import io.dddrive.core.ddd.model.Part
import io.dddrive.core.ddd.model.PartPersistenceProvider
import io.dddrive.core.ddd.model.RepositoryDirectory
import io.dddrive.core.ddd.model.RepositoryDirectorySPI

abstract class PartPersistenceProviderBase<P : Part<*>>(
	intfClass: Class<P>,
) : PartPersistenceProvider<P> {

	val directory: RepositoryDirectory get() = RepositoryDirectory.instance

	init {
		(directory as RepositoryDirectorySPI).addPartPersistenceProvider(intfClass, this)
	}

}
