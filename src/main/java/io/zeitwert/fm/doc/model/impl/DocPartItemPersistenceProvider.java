package io.zeitwert.fm.doc.model.impl;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.context.annotation.Configuration;

import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.DocPartItem;
import io.zeitwert.ddd.persistence.jooq.PartState;
import io.zeitwert.ddd.persistence.jooq.base.DocPartPersistenceProviderBase;
import io.zeitwert.fm.doc.model.db.Tables;
import io.zeitwert.fm.doc.model.db.tables.records.DocPartItemRecord;

@Configuration
public class DocPartItemPersistenceProvider extends DocPartPersistenceProviderBase<Doc, DocPartItem> {

	public DocPartItemPersistenceProvider(DSLContext dslContext) {
		super(DocPartItem.class, dslContext);
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
