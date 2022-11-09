package io.zeitwert.ddd.obj.model.impl;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.obj.model.ObjPartItem;
import io.zeitwert.ddd.obj.model.ObjPartItemRepository;
import io.zeitwert.ddd.obj.model.base.ObjPartItemBase;
import io.zeitwert.ddd.obj.model.base.ObjPartRepositoryBase;
import io.zeitwert.ddd.obj.model.db.Tables;
import io.zeitwert.ddd.obj.model.db.tables.records.ObjPartItemRecord;

@Component("objPartItemRepository")
public class ObjPartItemRepositoryImpl extends ObjPartRepositoryBase<Obj, ObjPartItem>
		implements ObjPartItemRepository {

	private static final String PART_TYPE = "obj_part_item";

	//@formatter:off
	protected ObjPartItemRepositoryImpl(
		final AppContext appContext,
		final DSLContext dslContext
	) {
		super(
			Obj.class,
			ObjPartItem.class,
			ObjPartItemBase.class,
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
	public ObjPartItem doCreate(Obj obj) {
		ObjPartItemRecord dbRecord = this.getDSLContext().newRecord(Tables.OBJ_PART_ITEM);
		return this.newPart(obj, dbRecord);
	}

	@Override
	public List<ObjPartItem> doLoad(Obj obj) {
		//@formatter:off
		Result<ObjPartItemRecord> dbRecords = this.getDSLContext()
			.selectFrom(Tables.OBJ_PART_ITEM)
			.where(Tables.OBJ_PART_ITEM.OBJ_ID.eq(obj.getId()))
			.orderBy(Tables.OBJ_PART_ITEM.PART_LIST_TYPE_ID, Tables.OBJ_PART_ITEM.SEQ_NR)
			.fetchInto(Tables.OBJ_PART_ITEM);
		//@formatter:on
		return dbRecords.map(dbRecord -> this.newPart(obj, dbRecord));
	}

}
