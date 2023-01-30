package io.zeitwert.ddd.doc.model.impl;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.DocPartItem;
import io.zeitwert.ddd.doc.model.DocPartItemRepository;
import io.zeitwert.ddd.doc.model.base.DocPartItemBase;
import io.zeitwert.ddd.doc.model.base.DocPartRepositoryBase;
import io.zeitwert.ddd.doc.model.db.Tables;
import io.zeitwert.ddd.doc.model.db.tables.records.DocPartItemRecord;

@Component("docPartItemRepository")
public class DocPartItemRepositoryImpl extends DocPartRepositoryBase<Doc, DocPartItem>
		implements DocPartItemRepository {

	private static final String PART_TYPE = "doc_part_item";

	//@formatter:off
	protected DocPartItemRepositoryImpl(
		final AppContext appContext,
		final DSLContext dslContext
	) {
		super(
			Doc.class,
			DocPartItem.class,
			DocPartItemBase.class,
			PART_TYPE,
			appContext,
			dslContext
		);
	}
	//@formatter:on

	@Override
	public boolean hasPartId() {
		return false;
	}

	@Override
	public DocPartItem doCreate(Doc doc) {
		DocPartItemRecord dbRecord = this.getDSLContext().newRecord(Tables.DOC_PART_ITEM);
		return this.newPart(doc, dbRecord);
	}

	@Override
	public List<DocPartItem> doLoad(Doc doc) {
		//@formatter:off
		Result<DocPartItemRecord> dbRecords = this.getDSLContext()
			.selectFrom(Tables.DOC_PART_ITEM)
			.where(Tables.DOC_PART_ITEM.DOC_ID.eq(doc.getId()))
			.orderBy(Tables.DOC_PART_ITEM.PART_LIST_TYPE_ID, Tables.DOC_PART_ITEM.SEQ_NR)
			.fetchInto(Tables.DOC_PART_ITEM);
		//@formatter:on
		return dbRecords.map(dbRecord -> this.newPart(doc, dbRecord));
	}

}
