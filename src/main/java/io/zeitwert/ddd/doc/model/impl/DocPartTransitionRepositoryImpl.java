package io.zeitwert.ddd.doc.model.impl;

import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.DocPartTransition;
import io.zeitwert.ddd.doc.model.DocPartTransitionRepository;
import io.zeitwert.ddd.doc.model.base.DocPartRepositoryBase;
import io.zeitwert.ddd.doc.model.base.DocPartTransitionBase;

@Component("docPartTransitionRepository")
public class DocPartTransitionRepositoryImpl extends DocPartRepositoryBase<Doc, DocPartTransition>
		implements DocPartTransitionRepository {

	private static final String PART_TYPE = "doc_part_transition";

	protected DocPartTransitionRepositoryImpl(AppContext appContext) {
		super(Doc.class, DocPartTransition.class, DocPartTransitionBase.class, PART_TYPE, appContext);
	}

}
