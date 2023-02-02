package io.zeitwert.ddd.doc.model.impl;

import java.util.List;

import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.DocPartItem;
import io.zeitwert.ddd.doc.model.DocPartItemRepository;
import io.zeitwert.ddd.doc.model.base.DocPartItemBase;
import io.zeitwert.ddd.doc.model.base.DocPartRepositoryBase;

@Component("docPartItemRepository")
public class DocPartItemRepositoryImpl extends DocPartRepositoryBase<Doc, DocPartItem>
		implements DocPartItemRepository {

	private static final String PART_TYPE = "doc_part_item";

	protected DocPartItemRepositoryImpl(final AppContext appContext, final DSLContext dslContext) {
		super(Doc.class, DocPartItem.class, DocPartItemBase.class, PART_TYPE, appContext, dslContext);
	}

	@Override
	public boolean hasPartId() {
		return false;
	}

	@Override
	public DocPartItem doCreate(Doc doc) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<DocPartItem> doLoad(Doc doc) {
		throw new UnsupportedOperationException();
	}

}
