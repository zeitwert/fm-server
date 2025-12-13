package io.zeitwert.fm.obj.model.impl;

import java.time.OffsetDateTime;
import java.util.List;

import org.jooq.Result;
import org.springframework.stereotype.Component;

import io.dddrive.jooq.ddd.PartState;
import io.dddrive.obj.model.Obj;
import io.dddrive.obj.model.ObjPartTransition;
import io.dddrive.obj.model.ObjPartTransitionRepository;
import io.dddrive.obj.model.base.ObjPartTransitionBase;
import io.zeitwert.fm.obj.model.base.FMObjPartRepositoryBase;
import io.zeitwert.fm.obj.model.db.Tables;
import io.zeitwert.fm.obj.model.db.tables.records.ObjPartTransitionRecord;

@Component("objPartTransitionRepository")
public class ObjPartTransitionRepositoryImpl extends FMObjPartRepositoryBase<Obj, ObjPartTransition>
		implements ObjPartTransitionRepository {

	private static final String PART_TYPE = "obj_part_transition";

	protected ObjPartTransitionRepositoryImpl() {
		super(Obj.class, ObjPartTransition.class, ObjPartTransitionBase.class, PART_TYPE);
	}

	@Override
	public void mapProperties() {
		super.mapProperties();
		this.mapField("tenantId", PartState.BASE, "tenant_id", Integer.class);
		this.mapField("user", PartState.BASE, "user_id", Integer.class);
		this.mapField("timestamp", PartState.BASE, "timestamp", OffsetDateTime.class);
	}

	@Override
	public ObjPartTransition doCreate(Obj obj) {
		ObjPartTransitionRecord dbRecord = this.dslContext().newRecord(Tables.OBJ_PART_TRANSITION);
		return this.getRepositorySPI().newPart(obj, new PartState(dbRecord));
	}

	@Override
	public List<ObjPartTransition> doLoad(Obj obj) {
		Result<ObjPartTransitionRecord> dbRecords = this.dslContext()
				.selectFrom(Tables.OBJ_PART_TRANSITION)
				.where(Tables.OBJ_PART_TRANSITION.OBJ_ID.eq(obj.getId()))
				.orderBy(Tables.OBJ_PART_TRANSITION.SEQ_NR)
				.fetchInto(Tables.OBJ_PART_TRANSITION);
		return dbRecords.map(dbRecord -> this.getRepositorySPI().newPart(obj, new PartState(dbRecord)));
	}

}
