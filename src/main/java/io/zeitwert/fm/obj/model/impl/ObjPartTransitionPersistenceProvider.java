package io.zeitwert.fm.obj.model.impl;

import java.time.OffsetDateTime;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.JSON;
import org.jooq.Result;
import org.springframework.context.annotation.Configuration;

import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.obj.model.ObjPartTransition;
import io.zeitwert.fm.obj.model.db.Tables;
import io.zeitwert.fm.obj.model.db.tables.records.ObjPartTransitionRecord;
import io.zeitwert.jooq.persistence.ObjPartPersistenceProviderBase;
import io.zeitwert.jooq.persistence.PartState;

@Configuration
public class ObjPartTransitionPersistenceProvider extends ObjPartPersistenceProviderBase<Obj, ObjPartTransition> {

	public ObjPartTransitionPersistenceProvider(DSLContext dslContext) {
		super(ObjPartTransition.class, dslContext);
		this.mapField("tenantId", PartState.BASE, "tenant_id", Integer.class);
		this.mapField("user", PartState.BASE, "user_id", Integer.class);
		this.mapField("timestamp", PartState.BASE, "timestamp", OffsetDateTime.class);
		this.mapField("changes", PartState.BASE, "changes", JSON.class);
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
