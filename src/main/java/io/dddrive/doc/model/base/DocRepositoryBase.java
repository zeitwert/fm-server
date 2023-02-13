
package io.dddrive.doc.model.base;

import javax.annotation.PostConstruct;

import io.dddrive.ddd.model.AggregateRepository;
import io.dddrive.ddd.model.base.AggregateRepositoryBase;
import io.dddrive.doc.model.Doc;
import io.dddrive.doc.model.DocRepository;

public abstract class DocRepositoryBase<D extends Doc, V extends Object>
		extends AggregateRepositoryBase<D, V>
		implements DocRepository<D, V> {

	protected DocRepositoryBase(
			Class<? extends AggregateRepository<D, V>> repoIntfClass,
			Class<? extends Doc> intfClass,
			Class<? extends Doc> baseClass,
			String aggregateTypeId) {
		super(repoIntfClass, intfClass, baseClass, aggregateTypeId);
	}

	@Override
	@PostConstruct
	public void registerPartRepositories() {
		this.addPartRepository(this.getTransitionRepository());
		this.addPartRepository(this.getItemRepository());
	}

}
