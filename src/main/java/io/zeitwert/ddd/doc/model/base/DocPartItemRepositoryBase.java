package io.zeitwert.ddd.doc.model.base;

import org.jooq.DSLContext;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.DocPartItem;
import io.zeitwert.ddd.doc.model.DocPartItemRepository;

public abstract class DocPartItemRepositoryBase extends DocPartRepositoryBase<Doc, DocPartItem>
		implements DocPartItemRepository {

	private static final String PART_TYPE = "doc_part_item";

	protected DocPartItemRepositoryBase(
			final AppContext appContext,
			final DSLContext dslContext) {
		super(
				Doc.class,
				DocPartItem.class,
				DocPartItemBase.class,
				PART_TYPE,
				appContext,
				dslContext);
	}

	@Override
	public boolean hasPartId() {
		return false;
	}

}
