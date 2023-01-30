package io.zeitwert.fm.obj.model.impl;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.obj.model.ObjPartTransition;
import io.zeitwert.ddd.obj.model.base.ObjPartTransitionRepositoryBase;
import io.zeitwert.fm.obj.model.db.Tables;
import io.zeitwert.fm.obj.model.db.tables.records.ObjPartTransitionRecord;

@Component("objPartTransitionRepository")
public class ObjPartTransitionRepositoryImpl extends ObjPartTransitionRepositoryBase {

	protected ObjPartTransitionRepositoryImpl(final AppContext appContext, final DSLContext dslContext) {
		super(appContext, dslContext);
	}

	@Override
	public ObjPartTransition doCreate(Obj obj) {
		ObjPartTransitionRecord dbRecord = this.getDSLContext().newRecord(Tables.OBJ_PART_TRANSITION);
		return this.newPart(obj, dbRecord);
	}

	@Override
	public List<ObjPartTransition> doLoad(Obj obj) {
		Result<ObjPartTransitionRecord> dbRecords = this.getDSLContext()
				.selectFrom(Tables.OBJ_PART_TRANSITION)
				.where(Tables.OBJ_PART_TRANSITION.OBJ_ID.eq(obj.getId()))
				.orderBy(Tables.OBJ_PART_TRANSITION.SEQ_NR)
				.fetchInto(Tables.OBJ_PART_TRANSITION);
		return dbRecords.map(dbRecord -> this.newPart(obj, dbRecord));
	}

}
