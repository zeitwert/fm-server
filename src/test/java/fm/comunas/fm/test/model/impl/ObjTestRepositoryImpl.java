
package fm.comunas.fm.test.model.impl;

import java.util.List;
import java.util.Optional;

import org.jooq.DSLContext;
import org.jooq.exception.NoDataFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.crnk.core.queryspec.QuerySpec;
import fm.comunas.ddd.app.service.api.AppContext;
import fm.comunas.ddd.obj.model.ObjPartItemRepository;
import fm.comunas.ddd.obj.model.ObjPartTransitionRepository;
import fm.comunas.ddd.property.model.enums.CodePartListType;
import fm.comunas.ddd.session.model.SessionInfo;
import fm.comunas.fm.obj.model.ObjPartNoteRepository;
import fm.comunas.fm.obj.model.base.FMObjRepositoryBase;
import fm.comunas.fm.test.model.ObjTest;
import fm.comunas.fm.test.model.ObjTestPartNodeRepository;
import fm.comunas.fm.test.model.ObjTestRepository;
import fm.comunas.fm.test.model.base.ObjTestBase;
import fm.comunas.fm.test.model.base.ObjTestFields;
import fm.comunas.fm.test.model.db.Tables;
import fm.comunas.fm.test.model.db.tables.records.ObjTestRecord;
import fm.comunas.fm.test.model.db.tables.records.ObjTestVRecord;

@Component("objTestRepository")
public class ObjTestRepositoryImpl extends FMObjRepositoryBase<ObjTest, ObjTestVRecord> implements ObjTestRepository {

	private static final String ITEM_TYPE = "obj_test";

	private final ObjTestPartNodeRepository nodeRepository;
	private final CodePartListType nodeListType;

	@Autowired
	//@formatter:off
	protected ObjTestRepositoryImpl(
		final AppContext appContext,
		final DSLContext dslContext,
		final ObjPartTransitionRepository transitionRepository,
		final ObjPartItemRepository itemRepository,
		final ObjPartNoteRepository noteRepository,
		final ObjTestPartNodeRepository nodeRepository
	) {
		super(
			ObjTestRepository.class,
			ObjTest.class,
			ObjTestBase.class,
			ITEM_TYPE,
			appContext,
			dslContext,
			transitionRepository,
			itemRepository,
			noteRepository
		);
		this.nodeRepository = nodeRepository;
		this.nodeListType = this.getAppContext().getPartListType(ObjTestFields.NODE_LIST);
	}
	//@formatter:on

	@Override
	public ObjTestPartNodeRepository getNodeRepository() {
		return this.nodeRepository;
	}

	@Override
	public CodePartListType getNodeListType() {
		return this.nodeListType;
	}

	@Override
	public ObjTest doCreate(SessionInfo sessionInfo) {
		return doCreate(sessionInfo, this.dslContext.newRecord(Tables.OBJ_TEST));
	}

	@Override
	public void doInitParts(ObjTest obj) {
		super.doInitParts(obj);
		this.getItemRepository().init(obj);
		this.nodeRepository.init(obj);
	}

	@Override
	public List<ObjTestVRecord> doFind(QuerySpec querySpec) {
		return this.doFind(Tables.OBJ_TEST_V, Tables.OBJ_TEST_V.ID, querySpec);
	}

	@Override
	public Optional<ObjTest> doLoad(SessionInfo sessionInfo, Integer objId) {
		require(objId != null, "objId not null");
		ObjTestRecord testRecord = this.dslContext.fetchOne(Tables.OBJ_TEST, Tables.OBJ_TEST.OBJ_ID.eq(objId));
		if (testRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + objId + "]");
		}
		return this.doLoad(sessionInfo, objId, testRecord);
	}

	@Override
	public void doLoadParts(ObjTest obj) {
		super.doLoadParts(obj);
		this.getItemRepository().load(obj);
		((ObjTestBase) obj).loadAreaSet(this.getItemRepository().getPartList(obj, this.getAreaSetType()));
		this.nodeRepository.load(obj);
		((ObjTestBase) obj).loadNodeList(this.nodeRepository.getPartList(obj, this.getNodeListType()));
	}

	@Override
	public void doStoreParts(ObjTest obj) {
		super.doStoreParts(obj);
		this.getItemRepository().store(obj);
		this.nodeRepository.store(obj);
	}

}
