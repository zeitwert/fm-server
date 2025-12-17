package io.dddrive.core.ddd.model.base

import io.dddrive.core.ddd.model.Aggregate
import io.dddrive.core.ddd.model.AggregateRepository
import io.dddrive.core.ddd.model.RepositoryDirectory
import io.dddrive.core.property.model.base.EntityWithPropertiesBase

/**
 * This superclass is factored out so that the repository is available in AggregateBase property initialisation
 */
abstract class AggregateWithRepositoryBase(
	open val repository: AggregateRepository<out Aggregate>,
) : EntityWithPropertiesBase() {

	override val directory: RepositoryDirectory
		get() = repository.directory

}
