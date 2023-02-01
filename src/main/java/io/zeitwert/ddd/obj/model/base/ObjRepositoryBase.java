
package io.zeitwert.ddd.obj.model.base;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.TableRecord;
import org.jooq.UpdatableRecord;
import org.jooq.exception.NoDataFoundException;
import org.jooq.Record;

import io.crnk.core.queryspec.FilterOperator;
import io.crnk.core.queryspec.PathSpec;
import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.aggregate.model.base.AggregateRepositoryBase;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.db.model.PersistenceProvider;
import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.obj.model.ObjPartItemRepository;
import io.zeitwert.ddd.obj.model.ObjPartTransitionRepository;
import io.zeitwert.ddd.obj.model.ObjRepository;
import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.part.model.enums.CodePartListTypeEnum;
import io.zeitwert.ddd.util.SqlUtils;
import io.zeitwert.fm.obj.model.db.Tables;
import io.zeitwert.fm.obj.model.db.tables.records.ObjRecord;

import java.util.List;

public abstract class ObjRepositoryBase<O extends Obj, V extends TableRecord<?>>
		extends AggregateRepositoryBase<O, V>
		implements ObjRepository<O, V> {

	private static final String OBJ_ID_SEQ = "obj_id_seq";

	private PersistenceProvider<O> persistenceProvider = new ObjPersistenceProviderBase<O>(null, null, null);
	private ObjPartTransitionRepository transitionRepository;
	private CodePartListType transitionListType;
	private ObjPartItemRepository itemRepository;

	protected ObjRepositoryBase(
			final Class<? extends AggregateRepository<O, V>> repoIntfClass,
			final Class<? extends Obj> intfClass,
			final Class<? extends Obj> baseClass,
			final String aggregateTypeId,
			final AppContext appContext,
			final DSLContext dslContext) {
		super(repoIntfClass, intfClass, baseClass, aggregateTypeId, appContext, dslContext);
	}

	@Override
	public PersistenceProvider<O> getPersistenceProvider() { // TODO: remove
		PersistenceProvider<O> pp = super.getPersistenceProvider();
		if (pp != null) {
			return pp;
		}
		return this.persistenceProvider;
	}

	@Override
	public ObjPartTransitionRepository getTransitionRepository() {
		if (this.transitionRepository == null) {
			this.transitionRepository = this.getAppContext().getBean(ObjPartTransitionRepository.class);
		}
		return this.transitionRepository;
	}

	@Override
	public CodePartListType getTransitionListType() {
		if (this.transitionListType == null) {
			this.transitionListType = CodePartListTypeEnum.getPartListType(ObjFields.TRANSITION_LIST);
		}
		return this.transitionListType;
	}

	@Override
	public ObjPartItemRepository getItemRepository() {
		if (this.itemRepository == null) {
			this.itemRepository = this.getAppContext().getBean(ObjPartItemRepository.class);
		}
		return this.itemRepository;
	}

	@Override
	public void registerPartRepositories() {
		this.addPartRepository(this.getTransitionRepository());
	}

	@Override
	public Integer nextAggregateId() {
		return this.getDSLContext().nextval(OBJ_ID_SEQ).intValue();
	}

	protected O doCreate(UpdatableRecord<?> extnRecord) {
		return this.newAggregate(this.getDSLContext().newRecord(Tables.OBJ), extnRecord);
	}

	protected O doLoad(Integer objId, UpdatableRecord<?> extnRecord) {
		ObjRecord objRecord = this.getDSLContext().fetchOne(Tables.OBJ, Tables.OBJ.ID.eq(objId));
		if (objRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + objId + "]");
		}
		return this.newAggregate(objRecord, extnRecord);
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
	protected List<V> doFind(Table<? extends Record> table, Field<Integer> idField, QuerySpec querySpec) {
		if (!SqlUtils.hasFilterFor(querySpec, "isClosed")) {
			querySpec.addFilter(PathSpec.of(ObjFields.CLOSED_AT.getName()).filter(FilterOperator.EQ, null));
		}
		return super.doFind(table, idField, querySpec);
	}

}
