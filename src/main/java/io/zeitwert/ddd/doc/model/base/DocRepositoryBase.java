
package io.zeitwert.ddd.doc.model.base;

import org.jooq.TableRecord;

import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.aggregate.model.base.AggregateRepositoryBase;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.DocRepository;

public abstract class DocRepositoryBase<D extends Doc, V extends TableRecord<?>>
		extends AggregateRepositoryBase<D, V>
		implements DocRepository<D, V> {

	protected DocRepositoryBase(
			Class<? extends AggregateRepository<D, V>> repoIntfClass,
			Class<? extends Doc> intfClass,
			Class<? extends Doc> baseClass,
			String aggregateTypeId,
			AppContext appContext) {
		super(repoIntfClass, intfClass, baseClass, aggregateTypeId, appContext);
	}

	@Override
	public void registerPartRepositories() {
		this.addPartRepository(DocRepository.getTransitionRepository());
	}

}
