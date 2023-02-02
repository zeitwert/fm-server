package io.zeitwert.fm.doc.model.impl;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.context.annotation.Configuration;

import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.DocPartItem;
import io.zeitwert.ddd.doc.model.DocPartItemRepository;
import io.zeitwert.ddd.doc.model.base.DocPartPersistenceProviderBase;
import io.zeitwert.ddd.doc.model.base.DocPartItemBase;
import io.zeitwert.ddd.persistence.jooq.PartState;
import io.zeitwert.fm.doc.model.db.Tables;
import io.zeitwert.fm.doc.model.db.tables.records.DocPartItemRecord;

@Configuration
public class DocPartItemPersistenceProvider extends DocPartPersistenceProviderBase<Doc, DocPartItem> {

	public DocPartItemPersistenceProvider(DSLContext dslContext) {
		super(Doc.class, DocPartItemRepository.class, DocPartItemBase.class, dslContext);
		this.mapField("itemId", BASE, "item_id", String.class);
	}

	@Override
	public boolean isReal() {
		return true;
	}

	@Override
	public Class<?> getEntityClass() {
		return DocPartItem.class;
	}

	@Override
	public DocPartItem doCreate(Doc doc) {
		DocPartItemRecord dbRecord = this.getDSLContext().newRecord(Tables.DOC_PART_ITEM);
		return this.newPart(doc, new PartState(dbRecord));
	}

	@Override
	public List<DocPartItem> doLoad(Doc doc) {
		Result<DocPartItemRecord> dbRecords = this.getDSLContext()
				.selectFrom(Tables.DOC_PART_ITEM)
				.where(Tables.DOC_PART_ITEM.DOC_ID.eq(doc.getId()))
				.orderBy(Tables.DOC_PART_ITEM.SEQ_NR)
				.fetchInto(Tables.DOC_PART_ITEM);
		return dbRecords.map(dbRecord -> this.newPart(doc, new PartState(dbRecord)));
	}

}
