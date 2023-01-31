
package io.zeitwert.fm.doc.model.impl;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.DocPartItem;
import io.zeitwert.ddd.doc.model.base.DocPartItemRepositoryBase;
import io.zeitwert.fm.doc.model.db.Tables;
import io.zeitwert.fm.doc.model.db.tables.records.DocPartItemRecord;

@Component("docPartItemRepository")
public class DocPartItemRepositoryImpl extends DocPartItemRepositoryBase {

	protected DocPartItemRepositoryImpl(final AppContext appContext, final DSLContext dslContext) {
		super(appContext, dslContext);
	}

	@Override
	public DocPartItem doCreate(Doc doc) {
		DocPartItemRecord dbRecord = this.getDSLContext().newRecord(Tables.DOC_PART_ITEM);
		return this.newPart(doc, dbRecord);
	}

	@Override
	public List<DocPartItem> doLoad(Doc doc) {
		Result<DocPartItemRecord> dbRecords = this.getDSLContext()
				.selectFrom(Tables.DOC_PART_ITEM)
				.where(Tables.DOC_PART_ITEM.DOC_ID.eq(doc.getId()))
				.orderBy(Tables.DOC_PART_ITEM.PART_LIST_TYPE_ID, Tables.DOC_PART_ITEM.SEQ_NR)
				.fetchInto(Tables.DOC_PART_ITEM);
		return dbRecords.map(dbRecord -> this.newPart(doc, dbRecord));
	}

}
