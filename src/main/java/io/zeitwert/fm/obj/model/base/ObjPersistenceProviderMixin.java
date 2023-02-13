package io.zeitwert.fm.obj.model.base;

import org.jooq.DSLContext;
import org.jooq.UpdatableRecord;
import org.jooq.exception.NoDataFoundException;

import io.dddrive.ddd.model.AggregatePersistenceProvider;
import io.dddrive.ddd.model.AggregateRepository;
import io.dddrive.ddd.model.base.AggregateRepositorySPI;
import io.dddrive.ddd.model.base.AggregateSPI;
import io.dddrive.jooq.ddd.AggregateState;
import io.dddrive.obj.model.Obj;
import io.zeitwert.fm.obj.model.db.Tables;
import io.zeitwert.fm.obj.model.db.tables.records.ObjRecord;

public interface ObjPersistenceProviderMixin<O extends Obj>
		extends AggregatePersistenceProvider<O> {

	static final String OBJ_ID_SEQ = "obj_id_seq";

	DSLContext dslContext();

	AggregateRepository<O, ?> getRepository();

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
