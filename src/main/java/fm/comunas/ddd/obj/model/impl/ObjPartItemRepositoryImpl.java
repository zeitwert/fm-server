package fm.comunas.ddd.obj.model.impl;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fm.comunas.ddd.app.service.api.AppContext;
import fm.comunas.ddd.obj.model.Obj;
import fm.comunas.ddd.obj.model.ObjPartItem;
import fm.comunas.ddd.obj.model.ObjPartItemRepository;
import fm.comunas.ddd.obj.model.base.ObjPartItemBase;
import fm.comunas.ddd.obj.model.base.ObjPartRepositoryBase;
import fm.comunas.ddd.obj.model.db.Tables;
import fm.comunas.ddd.obj.model.db.tables.records.ObjPartItemRecord;

import java.util.List;

@Component("objPartItemRepository")
public class ObjPartItemRepositoryImpl extends ObjPartRepositoryBase<Obj, ObjPartItem>
		implements ObjPartItemRepository {

	private static final String PART_TYPE = "obj_part_item";

	@Autowired
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
	public List<ObjPartItem> doLoad(Obj obj) {
		//@formatter:off
		Result<ObjPartItemRecord> dbRecords = this.dslContext
			.selectFrom(Tables.OBJ_PART_ITEM)
			.where(Tables.OBJ_PART_ITEM.OBJ_ID.eq(obj.getId()))
			.orderBy(Tables.OBJ_PART_ITEM.SEQ_NR)
			.fetchInto(Tables.OBJ_PART_ITEM);
		//@formatter:on
		return dbRecords.map(dbRecord -> this.newPart(obj, dbRecord));
	}

	@Override
	public ObjPartItem doCreate(Obj obj) {
		ObjPartItemRecord dbRecord = this.dslContext.newRecord(Tables.OBJ_PART_ITEM);
		return this.newPart(obj, dbRecord);
	}

}
