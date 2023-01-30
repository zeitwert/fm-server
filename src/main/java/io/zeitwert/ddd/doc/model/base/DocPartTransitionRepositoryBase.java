package io.zeitwert.ddd.doc.model.base;

import org.jooq.DSLContext;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.DocPartTransition;
import io.zeitwert.ddd.doc.model.DocPartTransitionRepository;

public abstract class DocPartTransitionRepositoryBase extends DocPartRepositoryBase<Doc, DocPartTransition>
		implements DocPartTransitionRepository {

	private static final String PART_TYPE = "doc_part_transition";

	protected DocPartTransitionRepositoryBase(final AppContext appContext, final DSLContext dslContext) {
		super(
				Doc.class,
				DocPartTransition.class,
				DocPartTransitionBase.class,
				PART_TYPE,
				appContext,
				dslContext);
	}

}
