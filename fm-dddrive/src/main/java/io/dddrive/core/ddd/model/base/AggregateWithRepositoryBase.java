package io.dddrive.core.ddd.model.base;

import io.dddrive.core.ddd.model.Aggregate;
import io.dddrive.core.ddd.model.AggregateRepository;
import io.dddrive.core.ddd.model.RepositoryDirectory;
import io.dddrive.core.property.model.base.EntityWithPropertiesBase;

/**
 * This superclass is factored out so that the repository is available in AggregateBase property initialisation
 */
public abstract class AggregateWithRepositoryBase extends EntityWithPropertiesBase {

	private final AggregateRepository<? extends Aggregate> repository;

	protected AggregateWithRepositoryBase(AggregateRepository<? extends Aggregate> repository) {
		this.repository = repository;
	}

	public RepositoryDirectory getDirectory() {
		return this.repository.getDirectory();
	}

	public AggregateRepository<? extends Aggregate> getRepository() {
		return this.repository;
	}

	protected <A extends Aggregate> AggregateRepository<A> getRepository(Class<A> aggregateClass) {
		return this.repository.getDirectory().getRepository(aggregateClass);
	}

}
