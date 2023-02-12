package io.zeitwert.fm.obj.model.impl;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.stereotype.Component;

import io.dddrive.app.service.api.AppContext;
import io.dddrive.jooq.ddd.PartState;
import io.dddrive.jooq.obj.JooqObjPartRepositoryBase;
import io.dddrive.obj.model.Obj;
import io.dddrive.obj.model.ObjPartItem;
import io.dddrive.obj.model.ObjPartItemRepository;
import io.dddrive.obj.model.base.ObjPartItemBase;
import io.zeitwert.fm.obj.model.db.Tables;
import io.zeitwert.fm.obj.model.db.tables.records.ObjPartItemRecord;

@Component("objPartItemRepository")
public class ObjPartItemRepositoryImpl extends JooqObjPartRepositoryBase<Obj, ObjPartItem>
		implements ObjPartItemRepository {

	private static final String PART_TYPE = "obj_part_item";

	protected ObjPartItemRepositoryImpl(AppContext appContext, DSLContext dslContext) {
		super(Obj.class, ObjPartItem.class, ObjPartItemBase.class, PART_TYPE, appContext, dslContext);
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
