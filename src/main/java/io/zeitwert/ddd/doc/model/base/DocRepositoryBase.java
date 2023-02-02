
package io.zeitwert.ddd.doc.model.base;

import org.jooq.TableRecord;

import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.aggregate.model.base.AggregateRepositoryBase;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.DocPartItemRepository;
import io.zeitwert.ddd.doc.model.DocPartTransitionRepository;
import io.zeitwert.ddd.doc.model.DocRepository;

public abstract class DocRepositoryBase<D extends Doc, V extends TableRecord<?>>
		extends AggregateRepositoryBase<D, V>
		implements DocRepository<D, V> {

	private DocPartTransitionRepository transitionRepository;
	private DocPartItemRepository itemRepository;

	protected DocRepositoryBase(
			Class<? extends AggregateRepository<D, V>> repoIntfClass,
			Class<? extends Doc> intfClass,
			Class<? extends Doc> baseClass,
			String aggregateTypeId,
			AppContext appContext) {
		super(repoIntfClass, intfClass, baseClass, aggregateTypeId, appContext);
	}

	@Override
	public DocPartTransitionRepository getTransitionRepository() {
		if (this.transitionRepository == null) {
			this.transitionRepository = AppContext.getInstance().getBean(DocPartTransitionRepository.class);
		}
		return this.transitionRepository;
	}

	@Override
	public DocPartItemRepository getItemRepository() {
		if (this.itemRepository == null) {
			this.itemRepository = AppContext.getInstance().getBean(DocPartItemRepository.class);
		}
		return this.itemRepository;
	}

	@Override
	public void registerPartRepositories() {
		this.addPartRepository(this.getTransitionRepository());
	}

	@Override
	public void doAfterStore(D doc) {
		super.doAfterStore(doc);
	}

}
