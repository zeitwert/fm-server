package io.zeitwert.fm.doc.model.impl;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.stereotype.Component;

import io.dddrive.app.service.api.AppContext;
import io.dddrive.doc.model.Doc;
import io.dddrive.doc.model.DocPartItem;
import io.dddrive.doc.model.DocPartItemRepository;
import io.dddrive.doc.model.base.DocPartItemBase;
import io.dddrive.jooq.ddd.PartState;
import io.zeitwert.fm.doc.model.base.FMDocPartRepositoryBase;
import io.zeitwert.fm.doc.model.db.Tables;
import io.zeitwert.fm.doc.model.db.tables.records.DocPartItemRecord;

@Component("docPartItemRepository")
public class DocPartItemRepositoryImpl extends FMDocPartRepositoryBase<Doc, DocPartItem>
		implements DocPartItemRepository {

	private static final String PART_TYPE = "doc_part_item";

	protected DocPartItemRepositoryImpl(AppContext appContext, DSLContext dslContext) {
		super(Doc.class, DocPartItem.class, DocPartItemBase.class, PART_TYPE, appContext, dslContext);
	}

	@Override
	public boolean hasPartId() {
		return false;
	}

	@Override
	public void mapProperties() {
		super.mapProperties();
		this.mapField("itemId", PartState.BASE, "item_id", String.class);
	}

	@Override
	public DocPartItem doCreate(Doc doc) {
		DocPartItemRecord dbRecord = this.dslContext().newRecord(Tables.DOC_PART_ITEM);
		return this.getRepositorySPI().newPart(doc, new PartState(dbRecord));
	}

	@Override
	public List<DocPartItem> doLoad(Doc doc) {
		Result<DocPartItemRecord> dbRecords = this.dslContext()
				.selectFrom(Tables.DOC_PART_ITEM)
				.where(Tables.DOC_PART_ITEM.DOC_ID.eq(doc.getId()))
				.orderBy(Tables.DOC_PART_ITEM.SEQ_NR)
				.fetchInto(Tables.DOC_PART_ITEM);
		return dbRecords.map(dbRecord -> this.getRepositorySPI().newPart(doc, new PartState(dbRecord)));
	}

}
