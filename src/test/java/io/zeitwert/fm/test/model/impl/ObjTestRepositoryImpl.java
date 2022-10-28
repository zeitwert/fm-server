
package io.zeitwert.fm.test.model.impl;

import static io.zeitwert.ddd.util.Check.requireThis;

import java.util.List;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.jooq.exception.NoDataFoundException;
import org.springframework.stereotype.Component;

import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.obj.model.ObjPartItemRepository;
import io.zeitwert.ddd.obj.model.ObjPartTransitionRepository;
import io.zeitwert.ddd.property.model.enums.CodePartListType;
import io.zeitwert.fm.collaboration.model.ObjNoteRepository;
import io.zeitwert.fm.obj.model.base.FMObjRepositoryBase;
import io.zeitwert.fm.test.model.ObjTest;
import io.zeitwert.fm.test.model.ObjTestPartNodeRepository;
import io.zeitwert.fm.test.model.ObjTestRepository;
import io.zeitwert.fm.test.model.base.ObjTestBase;
import io.zeitwert.fm.test.model.base.ObjTestFields;
import io.zeitwert.fm.test.model.db.Tables;
import io.zeitwert.fm.test.model.db.tables.records.ObjTestRecord;
import io.zeitwert.fm.test.model.db.tables.records.ObjTestVRecord;

@Component("objTestRepository")
public class ObjTestRepositoryImpl extends FMObjRepositoryBase<ObjTest, ObjTestVRecord> implements ObjTestRepository {

	private static final String AGGREGATE_TYPE = "obj_test";

	private final ObjTestPartNodeRepository nodeRepository;
	private final CodePartListType nodeListType;

	//@formatter:off
	protected ObjTestRepositoryImpl(
		final AppContext appContext,
		final DSLContext dslContext,
		final ObjPartTransitionRepository transitionRepository,
		final ObjPartItemRepository itemRepository,
		final ObjTestPartNodeRepository nodeRepository,
		final ObjNoteRepository noteRepository
	) {
		super(
			ObjTestRepository.class,
			ObjTest.class,
			ObjTestBase.class,
			AGGREGATE_TYPE,
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
	@PostConstruct
	public void registerPartRepositories() {
		super.registerPartRepositories();
		this.addPartRepository(this.getItemRepository());
		this.addPartRepository(this.getNodeRepository());
	}

	@Override
	public ObjTestPartNodeRepository getNodeRepository() {
		return this.nodeRepository;
	}

	@Override
	public CodePartListType getNodeListType() {
		return this.nodeListType;
	}

	@Override
	public ObjTest doCreate() {
		return this.doCreate(this.getDSLContext().newRecord(Tables.OBJ_TEST));
	}

	@Override
	public ObjTest doLoad(Integer objId) {
		requireThis(objId != null, "objId not null");
		ObjTestRecord testRecord = this.getDSLContext().fetchOne(Tables.OBJ_TEST, Tables.OBJ_TEST.OBJ_ID.eq(objId));
		if (testRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + objId + "]");
		}
		return this.doLoad(objId, testRecord);
	}

	@Override
	public List<ObjTestVRecord> doFind(QuerySpec querySpec) {
		return this.doFind(Tables.OBJ_TEST_V, Tables.OBJ_TEST_V.ID, querySpec);
	}

}
