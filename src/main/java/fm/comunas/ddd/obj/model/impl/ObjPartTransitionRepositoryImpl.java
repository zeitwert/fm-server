package fm.comunas.ddd.obj.model.impl;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fm.comunas.ddd.app.service.api.AppContext;
import fm.comunas.ddd.obj.model.Obj;
import fm.comunas.ddd.obj.model.ObjPartTransition;
import fm.comunas.ddd.obj.model.ObjPartTransitionRepository;
import fm.comunas.ddd.obj.model.base.ObjPartRepositoryBase;
import fm.comunas.ddd.obj.model.base.ObjPartTransitionBase;
import fm.comunas.ddd.obj.model.db.Tables;
import fm.comunas.ddd.obj.model.db.tables.records.ObjPartTransitionRecord;

import java.util.List;

@Component("objPartTransitionRepository")
public class ObjPartTransitionRepositoryImpl extends ObjPartRepositoryBase<Obj, ObjPartTransition>
		implements ObjPartTransitionRepository {

	private static final String PART_TYPE = "obj_part_transition";

	@Autowired
	//@formatter:off
	protected ObjPartTransitionRepositoryImpl(
		final AppContext appContext,
		final DSLContext dslContext
	) {
		super(
			Obj.class,
			ObjPartTransition.class,
			ObjPartTransitionBase.class,
			PART_TYPE,
			appContext,
			dslContext
		);
	}
	//@formatter:on

	@Override
	public List<ObjPartTransition> doLoad(Obj obj) {
		//@formatter:off
		Result<ObjPartTransitionRecord> dbRecords = this.dslContext
			.selectFrom(Tables.OBJ_PART_TRANSITION)
			.where(Tables.OBJ_PART_TRANSITION.OBJ_ID.eq(obj.getId()))
			.orderBy(Tables.OBJ_PART_TRANSITION.SEQ_NR)
			.fetchInto(Tables.OBJ_PART_TRANSITION);
		//@formatter:on
		return dbRecords.map(dbRecord -> this.newPart(obj, dbRecord));
	}

	@Override
	public ObjPartTransition doCreate(Obj obj) {
		ObjPartTransitionRecord dbRecord = this.dslContext.newRecord(Tables.OBJ_PART_TRANSITION);
		return this.newPart(obj, dbRecord);
	}

}
