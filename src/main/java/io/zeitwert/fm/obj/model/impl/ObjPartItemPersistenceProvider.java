package io.zeitwert.fm.obj.model.impl;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.context.annotation.Configuration;

import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.obj.model.ObjPartItem;
import io.zeitwert.ddd.persistence.jooq.PartState;
import io.zeitwert.ddd.persistence.jooq.base.ObjPartPersistenceProviderBase;
import io.zeitwert.fm.obj.model.db.Tables;
import io.zeitwert.fm.obj.model.db.tables.records.ObjPartItemRecord;

@Configuration
public class ObjPartItemPersistenceProvider extends ObjPartPersistenceProviderBase<Obj, ObjPartItem> {

	public ObjPartItemPersistenceProvider(DSLContext dslContext) {
		super(ObjPartItem.class, dslContext);
		this.mapField("itemId", PartState.BASE, "item_id", String.class);
	}

	@Override
	public ObjPartItem doCreate(Obj obj) {
		ObjPartItemRecord dbRecord = this.dslContext().newRecord(Tables.OBJ_PART_ITEM);
		return this.getRepositorySPI().newPart(obj, new PartState(dbRecord));
	}

	@Override
	public List<ObjPartItem> doLoad(Obj obj) {
		Result<ObjPartItemRecord> dbRecords = this.dslContext()
				.selectFrom(Tables.OBJ_PART_ITEM)
				.where(Tables.OBJ_PART_ITEM.OBJ_ID.eq(obj.getId()))
				.orderBy(Tables.OBJ_PART_ITEM.SEQ_NR)
				.fetchInto(Tables.OBJ_PART_ITEM);
		return dbRecords.map(dbRecord -> this.getRepositorySPI().newPart(obj, new PartState(dbRecord)));
	}

}
