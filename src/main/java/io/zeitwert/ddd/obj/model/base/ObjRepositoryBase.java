
package io.zeitwert.ddd.obj.model.base;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.UpdatableRecord;
import org.jooq.exception.NoDataFoundException;

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

import java.util.Optional;

public abstract class ObjRepositoryBase<O extends Obj, V extends Record> extends AggregateRepositoryBase<O, V>
		implements ObjRepository<O, V> {

	private static final String OBJ_ID_SEQ = "obj_id_seq";

	private final ObjPartTransitionRepository transitionRepository;
	private final CodePartListType transitionListType;
	private final ObjPartItemRepository itemRepository;
	private final CodePartListType areaSetType;

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
		this.areaSetType = this.getAppContext().getPartListType(ObjFields.AREA_SET);
	}
	//@formatter:on

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
	public CodePartListType getAreaSetType() {
		return this.areaSetType;
	}

	protected ObjRepositoryUtil getUtil() {
		return ObjRepositoryUtil.getInstance();
	}

	protected Optional<O> doLoad(SessionInfo sessionInfo, Integer objId, UpdatableRecord<?> extnRecord) {
		ObjRecord objRecord = this.dslContext.fetchOne(Tables.OBJ, Tables.OBJ.ID.eq(objId));
		if (objRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + objId + "]");
		}
		return Optional.of(newAggregate(sessionInfo, objRecord, extnRecord));
	}

	@Override
	public void doLoadParts(O obj) {
		super.doLoadParts(obj);
		this.transitionRepository.load(obj);
		((ObjBase) obj).loadTransitionList(this.transitionRepository.getPartList(obj, this.getTransitionListType()));
	}

	@Override
	public Integer nextAggregateId() {
		return this.dslContext.nextval(OBJ_ID_SEQ).intValue();
	}

	protected O doCreate(SessionInfo sessionInfo, UpdatableRecord<?> extnRecord) {
		return newAggregate(sessionInfo, this.dslContext.newRecord(Tables.OBJ), extnRecord);
	}

	@Override
	public void doInitParts(O obj) {
		super.doInitParts(obj);
		this.transitionRepository.init(obj);
	}

	@Override
	public void doStoreParts(O obj) {
		super.doStoreParts(obj);
		this.transitionRepository.store(obj);
	}

	@Override
	public void afterStore(O obj) {
		super.afterStore(obj);
	}

}
