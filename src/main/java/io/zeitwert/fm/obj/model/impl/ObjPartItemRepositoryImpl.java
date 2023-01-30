package io.zeitwert.fm.obj.model.impl;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.obj.model.ObjPartItem;
import io.zeitwert.ddd.obj.model.base.ObjPartItemRepositoryBase;
import io.zeitwert.fm.obj.model.db.Tables;
import io.zeitwert.fm.obj.model.db.tables.records.ObjPartItemRecord;

@Component("objPartItemRepository")
public class ObjPartItemRepositoryImpl extends ObjPartItemRepositoryBase {

	protected ObjPartItemRepositoryImpl(final AppContext appContext, final DSLContext dslContext) {
		super(appContext, dslContext);
	}

	@Override
	public ObjPartItem doCreate(Obj obj) {
		ObjPartItemRecord dbRecord = this.getDSLContext().newRecord(Tables.OBJ_PART_ITEM);
		return this.newPart(obj, dbRecord);
	}

	@Override
	public List<ObjPartItem> doLoad(Obj obj) {
		Result<ObjPartItemRecord> dbRecords = this.getDSLContext()
				.selectFrom(Tables.OBJ_PART_ITEM)
				.where(Tables.OBJ_PART_ITEM.OBJ_ID.eq(obj.getId()))
				.orderBy(Tables.OBJ_PART_ITEM.PART_LIST_TYPE_ID, Tables.OBJ_PART_ITEM.SEQ_NR)
				.fetchInto(Tables.OBJ_PART_ITEM);
		return dbRecords.map(dbRecord -> this.newPart(obj, dbRecord));
	}

}
