
package io.zeitwert.ddd.doc.model.base;

import org.jooq.DSLContext;
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

	private static final String DOC_ID_SEQ = "doc_id_seq";

	private DocPartTransitionRepository transitionRepository;
	private DocPartItemRepository itemRepository;

	protected DocRepositoryBase(
			final Class<? extends AggregateRepository<D, V>> repoIntfClass,
			final Class<? extends Doc> intfClass,
			final Class<? extends Doc> baseClass,
			final String aggregateTypeId,
			final AppContext appContext,
			final DSLContext dslContext) {
		super(repoIntfClass, intfClass, baseClass, aggregateTypeId, appContext, dslContext);
	}

	@Override
	public DocPartTransitionRepository getTransitionRepository() {
		if (this.transitionRepository == null) {
			this.transitionRepository = this.getAppContext().getBean(DocPartTransitionRepository.class);
		}
		return this.transitionRepository;
	}

	@Override
	public DocPartItemRepository getItemRepository() {
		if (this.itemRepository == null) {
			this.itemRepository = this.getAppContext().getBean(DocPartItemRepository.class);
		}
		return this.itemRepository;
	}

	@Override
	public void registerPartRepositories() {
		this.addPartRepository(this.getTransitionRepository());
	}

	@Override
	public Integer nextAggregateId() {
		return this.getDSLContext().nextval(DOC_ID_SEQ).intValue();
	}

	@Override
	public void doAfterStore(D doc) {
		super.doAfterStore(doc);
	}

}
