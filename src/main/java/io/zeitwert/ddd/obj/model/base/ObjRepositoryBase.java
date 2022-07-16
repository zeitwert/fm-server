
package io.zeitwert.ddd.obj.model.base;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.UpdatableRecord;
import org.jooq.exception.NoDataFoundException;

import io.crnk.core.queryspec.FilterOperator;
import io.crnk.core.queryspec.PathSpec;
import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.aggregate.model.base.AggregateRepositoryBase;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.obj.model.ObjPartItemRepository;
import io.zeitwert.ddd.obj.model.ObjPartTransitionRepository;
import io.zeitwert.ddd.obj.model.ObjRepository;
import io.zeitwert.ddd.obj.model.db.Tables;
import io.zeitwert.ddd.obj.model.db.tables.records.ObjRecord;
import io.zeitwert.ddd.property.model.enums.CodePartListType;
import io.zeitwert.ddd.session.model.SessionInfo;

import java.util.List;

public abstract class ObjRepositoryBase<O extends Obj, V extends Record> extends AggregateRepositoryBase<O, V>
		implements ObjRepository<O, V> {

	private static final String OBJ_ID_SEQ = "obj_id_seq";

	private final ObjPartTransitionRepository transitionRepository;
	private final CodePartListType transitionListType;
	private final ObjPartItemRepository itemRepository;

	//@formatter:off
	protected ObjRepositoryBase(
		final Class<? extends AggregateRepository<O, V>> repoIntfClass,
		final Class<? extends Obj> intfClass,
		final Class<? extends Obj> baseClass,
		final String aggregateTypeId,
		final AppContext appContext,
		final DSLContext dslContext,
		final ObjPartTransitionRepository transitionRepository,
		final ObjPartItemRepository itemRepository
	) {
		super(repoIntfClass, intfClass, baseClass, aggregateTypeId, appContext, dslContext);
		this.transitionRepository = transitionRepository;
		this.transitionListType = this.getAppContext().getPartListType(ObjFields.TRANSITION_LIST);
		this.itemRepository = itemRepository;
	}
	//@formatter:on

	@Override
	public void registerPartRepositories() {
		this.addPartRepository(this.getTransitionRepository());
	}

	@Override
	public ObjPartTransitionRepository getTransitionRepository() {
		return this.transitionRepository;
	}

	@Override
	public CodePartListType getTransitionListType() {
		return this.transitionListType;
	}

	@Override
	public ObjPartItemRepository getItemRepository() {
		return this.itemRepository;
	}

	@Override
	public Integer nextAggregateId() {
		return this.getDSLContext().nextval(OBJ_ID_SEQ).intValue();
	}

	protected O doCreate(SessionInfo sessionInfo, UpdatableRecord<?> extnRecord) {
		return newAggregate(sessionInfo, this.getDSLContext().newRecord(Tables.OBJ), extnRecord);
	}

	protected O doLoad(SessionInfo sessionInfo, Integer objId, UpdatableRecord<?> extnRecord) {
		ObjRecord objRecord = this.getDSLContext().fetchOne(Tables.OBJ, Tables.OBJ.ID.eq(objId));
		if (objRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + objId + "]");
		}
		return newAggregate(sessionInfo, objRecord, extnRecord);
	}

	@Override
	public void doAfterStore(O obj) {
		super.doAfterStore(obj);
	}

	@Override
	public void delete(O obj) {
		obj.delete();
		this.store(obj);
	}

	@Override
	protected List<V> doFind(SessionInfo sessionInfo, Table<V> table, Field<Integer> idField, QuerySpec querySpec) {
		querySpec.addFilter(PathSpec.of(ObjFields.CLOSED_AT.getName()).filter(FilterOperator.EQ, null));
		return super.doFind(sessionInfo, table, idField, querySpec);
	}

}
