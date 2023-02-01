package io.zeitwert.ddd.obj.model.base;

import static io.zeitwert.ddd.util.Check.assertThis;

import java.time.OffsetDateTime;

import org.jooq.DSLContext;
import org.jooq.UpdatableRecord;
import org.jooq.exception.NoDataFoundException;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.db.model.AggregateState;
import io.zeitwert.ddd.db.model.jooq.AggregateStateImpl;
import io.zeitwert.ddd.db.model.jooq.PersistenceProviderBase;
import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.obj.model.ObjPartTransition;
import io.zeitwert.fm.obj.model.db.Tables;
import io.zeitwert.fm.obj.model.db.tables.records.ObjRecord;

public /* TODO abstract */ class ObjPersistenceProviderBase<O extends Obj> extends PersistenceProviderBase<O> {

	public ObjPersistenceProviderBase(
			Class<? extends AggregateRepository<O, ?>> repoIntfClass,
			Class<? extends Aggregate> baseClass,
			DSLContext dslContext) {
		super(repoIntfClass, baseClass, dslContext);
		this.mapField("id", DbTableType.BASE, "id", Integer.class);
		this.mapField("objTypeId", DbTableType.BASE, "obj_type_id", String.class);
		this.mapField("tenant", DbTableType.BASE, "tenant_id", Integer.class);
		this.mapField("owner", DbTableType.BASE, "owner_id", Integer.class);
		this.mapField("caption", DbTableType.BASE, "caption", String.class);
		this.mapField("version", DbTableType.BASE, "version", Integer.class);
		this.mapField("createdByUser", DbTableType.BASE, "created_by_user_id", Integer.class);
		this.mapField("createdAt", DbTableType.BASE, "created_at", OffsetDateTime.class);
		this.mapField("modifiedByUser", DbTableType.BASE, "modified_by_user_id", Integer.class);
		this.mapField("modifiedAt", DbTableType.BASE, "modified_at", OffsetDateTime.class);
		this.mapField("closedByUser", DbTableType.BASE, "closed_by_user_id", Integer.class);
		this.mapField("closedAt", DbTableType.BASE, "closed_at", OffsetDateTime.class);
		this.mapCollection("transitionList", "obj.transitionList", ObjPartTransition.class);
	}

	@Override
	public Class<?> getEntityClass() {
		return null;
	}

	@Override
	public AggregateState getAggregateState(Aggregate aggregate) {
		return null;
	}

	@Override
	public O doCreate() {
		assertThis(false, "nope");
		return null;
	}

	@Override
	public O doLoad(Integer id) {
		assertThis(false, "nope");
		return null;
	}

	protected O doCreate(UpdatableRecord<?> extnRecord) {
		ObjRecord objRecord = this.getDSLContext().newRecord(Tables.OBJ);
		AggregateState state = new AggregateStateImpl(objRecord, extnRecord);
		return this.newAggregate(state);
	}

	protected O doLoad(Integer objId, UpdatableRecord<?> extnRecord) {
		ObjRecord objRecord = this.getDSLContext().fetchOne(Tables.OBJ, Tables.OBJ.ID.eq(objId));
		if (objRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + objId + "]");
		}
		AggregateState state = new AggregateStateImpl(objRecord, extnRecord);
		return this.newAggregate(state);
	}

}
