package io.zeitwert.fm.obj.model.impl;

import java.time.OffsetDateTime;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.JSON;
import org.jooq.Result;
import org.springframework.context.annotation.Configuration;

import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.obj.model.ObjPartTransition;
import io.zeitwert.ddd.obj.model.ObjPartTransitionRepository;
import io.zeitwert.ddd.obj.model.base.ObjPartPersistenceProviderBase;
import io.zeitwert.ddd.obj.model.base.ObjPartTransitionBase;
import io.zeitwert.ddd.persistence.jooq.PartState;
import io.zeitwert.fm.obj.model.db.Tables;
import io.zeitwert.fm.obj.model.db.tables.records.ObjPartTransitionRecord;

@Configuration
public class ObjPartTransitionPersistenceProvider extends ObjPartPersistenceProviderBase<Obj, ObjPartTransition> {

	public ObjPartTransitionPersistenceProvider(DSLContext dslContext) {
		super(Obj.class, ObjPartTransitionRepository.class, ObjPartTransitionBase.class, dslContext);
		this.mapField("tenantId", BASE, "tenant_id", Integer.class);
		this.mapField("user", BASE, "user_id", Integer.class);
		this.mapField("timestamp", BASE, "timestamp", OffsetDateTime.class);
		this.mapField("changes", BASE, "changes", JSON.class);
	}

	@Override
	public Class<?> getEntityClass() {
		return ObjPartTransition.class;
	}

	@Override
	public ObjPartTransition doCreate(Obj obj) {
		ObjPartTransitionRecord dbRecord = this.getDSLContext().newRecord(Tables.OBJ_PART_TRANSITION);
		return this.newPart(obj, new PartState(dbRecord));
	}

	@Override
	public List<ObjPartTransition> doLoad(Obj obj) {
		Result<ObjPartTransitionRecord> dbRecords = this.getDSLContext()
				.selectFrom(Tables.OBJ_PART_TRANSITION)
				.where(Tables.OBJ_PART_TRANSITION.OBJ_ID.eq(obj.getId()))
				.orderBy(Tables.OBJ_PART_TRANSITION.SEQ_NR)
				.fetchInto(Tables.OBJ_PART_TRANSITION);
		return dbRecords.map(dbRecord -> this.newPart(obj, new PartState(dbRecord)));
	}

}
