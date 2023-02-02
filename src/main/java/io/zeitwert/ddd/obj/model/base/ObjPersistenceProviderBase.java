package io.zeitwert.ddd.obj.model.base;

import java.time.OffsetDateTime;

import org.jooq.DSLContext;
import org.jooq.UpdatableRecord;
import org.jooq.exception.NoDataFoundException;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.aggregate.model.base.AggregateRepositorySPI;
import io.zeitwert.ddd.aggregate.model.base.AggregateSPI;
import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.obj.model.ObjPartTransition;
import io.zeitwert.ddd.persistence.jooq.AggregateState;
import io.zeitwert.ddd.persistence.jooq.base.AggregatePersistenceProviderBase;
import io.zeitwert.fm.obj.model.db.Tables;
import io.zeitwert.fm.obj.model.db.tables.records.ObjRecord;

public abstract class ObjPersistenceProviderBase<O extends Obj> extends AggregatePersistenceProviderBase<O> {

	public ObjPersistenceProviderBase(
			Class<? extends AggregateRepository<O, ?>> repoIntfClass,
			Class<? extends Aggregate> baseClass,
			DSLContext dslContext) {
		super(repoIntfClass, baseClass, dslContext);
		this.mapField("id", BASE, "id", Integer.class);
		this.mapField("objTypeId", BASE, "obj_type_id", String.class);
		this.mapField("tenant", BASE, "tenant_id", Integer.class);
		this.mapField("owner", BASE, "owner_id", Integer.class);
		this.mapField("caption", BASE, "caption", String.class);
		this.mapField("version", BASE, "version", Integer.class);
		this.mapField("createdByUser", BASE, "created_by_user_id", Integer.class);
		this.mapField("createdAt", BASE, "created_at", OffsetDateTime.class);
		this.mapField("modifiedByUser", BASE, "modified_by_user_id", Integer.class);
		this.mapField("modifiedAt", BASE, "modified_at", OffsetDateTime.class);
		this.mapField("closedByUser", BASE, "closed_by_user_id", Integer.class);
		this.mapField("closedAt", BASE, "closed_at", OffsetDateTime.class);
		this.mapCollection("transitionList", "obj.transitionList", ObjPartTransition.class);
	}

	@Override
	public Class<?> getEntityClass() {
		return null;
	}

	@SuppressWarnings("unchecked")
	protected O doCreate(UpdatableRecord<?> extnRecord) {
		ObjRecord objRecord = this.getDSLContext().newRecord(Tables.OBJ);
		AggregateState state = new AggregateState(objRecord, extnRecord);
		return ((AggregateRepositorySPI<O, ?>) this.getRepository()).newAggregate(state);
	}

	@Override
	public final void doInit(O aggregate, Integer id, Integer tenantId) {
		ObjBase obj = (ObjBase) aggregate;
		try {
			obj.disableCalc();
			obj.objTypeId.setValue(obj.getRepository().getAggregateType().getId());
			obj.id.setValue(id);
			obj.tenant.setId(tenantId);
			AggregateState state = (AggregateState) obj.getAggregateState();
			UpdatableRecord<?> extnRecord = state.extnRecord();
			if (extnRecord != null) {
				extnRecord.setValue(ObjExtnFields.OBJ_ID, id);
				// obj_tenant does not have a tenant_id field
				if (extnRecord.field(ObjExtnFields.TENANT_ID) != null) {
					extnRecord.setValue(ObjExtnFields.TENANT_ID, tenantId);
				}
			}
		} finally {
			obj.enableCalc();
		}
	}

	@SuppressWarnings("unchecked")
	protected O doLoad(Integer objId, UpdatableRecord<?> extnRecord) {
		ObjRecord objRecord = this.getDSLContext().fetchOne(Tables.OBJ, Tables.OBJ.ID.eq(objId));
		if (objRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + objId + "]");
		}
		AggregateState state = new AggregateState(objRecord, extnRecord);
		return ((AggregateRepositorySPI<O, ?>) this.getRepository()).newAggregate(state);
	}

	@Override
	public final void doStore(O obj) {
		AggregateState state = (AggregateState) ((AggregateSPI) obj).getAggregateState();
		state.baseRecord().store();
		if (state.extnRecord() != null) {
			state.extnRecord().store();
		}
	}

}
