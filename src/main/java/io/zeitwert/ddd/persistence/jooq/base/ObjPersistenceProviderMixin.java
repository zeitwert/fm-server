package io.zeitwert.ddd.persistence.jooq.base;

import org.jooq.UpdatableRecord;
import org.jooq.exception.NoDataFoundException;

import io.zeitwert.ddd.aggregate.model.base.AggregateRepositorySPI;
import io.zeitwert.ddd.aggregate.model.base.AggregateSPI;
import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.persistence.jooq.AggregateState;
import io.zeitwert.fm.obj.model.db.Tables;
import io.zeitwert.fm.obj.model.db.tables.records.ObjRecord;

public interface ObjPersistenceProviderMixin<O extends Obj>
		extends AggregatePersistenceProviderMixin<O> {

	static final String OBJ_ID_SEQ = "obj_id_seq";

	@Override
	default Integer nextAggregateId() {
		return this.dslContext().nextval(OBJ_ID_SEQ).intValue();
	}

	@SuppressWarnings("unchecked")
	default O doCreate(UpdatableRecord<?> extnRecord) {
		ObjRecord objRecord = this.dslContext().newRecord(Tables.OBJ);
		AggregateState state = new AggregateState(objRecord, extnRecord);
		return ((AggregateRepositorySPI<O, ?>) this.getRepository()).newAggregate(state);
	}

	@SuppressWarnings("unchecked")
	default O doLoad(Integer objId, UpdatableRecord<?> extnRecord) {
		ObjRecord objRecord = this.dslContext().fetchOne(Tables.OBJ, Tables.OBJ.ID.eq(objId));
		if (objRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + objId + "]");
		}
		AggregateState state = new AggregateState(objRecord, extnRecord);
		return ((AggregateRepositorySPI<O, ?>) this.getRepository()).newAggregate(state);
	}

	@Override
	default void doStore(O obj) {
		AggregateState state = (AggregateState) ((AggregateSPI) obj).getAggregateState();
		state.baseRecord().store();
		if (state.extnRecord() != null) {
			state.extnRecord().store();
		}
	}

}
