
package io.zeitwert.fm.test.model.impl;

import static io.zeitwert.ddd.util.Check.requireThis;

import java.util.List;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.jooq.exception.NoDataFoundException;
import org.springframework.stereotype.Component;

import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.part.model.enums.CodePartListTypeEnum;
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

	private CodePartListType countrySetType;
	private ObjTestPartNodeRepository nodeRepository;
	private CodePartListType nodeListType;

	protected ObjTestRepositoryImpl(
			final AppContext appContext,
			final DSLContext dslContext) {
		super(
				ObjTestRepository.class,
				ObjTest.class,
				ObjTestBase.class,
				AGGREGATE_TYPE,
				appContext,
				dslContext);
	}

	@Override
	public CodePartListType getCountrySetType() {
		if (this.countrySetType == null) {
			this.countrySetType = CodePartListTypeEnum.getPartListType(ObjTestFields.COUNTRY_SET);
		}
		return this.countrySetType;
	}

	@Override
	public ObjTestPartNodeRepository getNodeRepository() {
		if (this.nodeRepository == null) {
			this.nodeRepository = this.getAppContext().getBean(ObjTestPartNodeRepository.class);
		}
		return this.nodeRepository;
	}

	@Override
	public CodePartListType getNodeListType() {
		if (this.nodeListType == null) {
			this.nodeListType = CodePartListTypeEnum.getPartListType(ObjTestFields.NODE_LIST);
		}
		return this.nodeListType;
	}

	@Override
	@PostConstruct
	public void registerPartRepositories() {
		super.registerPartRepositories();
		this.addPartRepository(this.getItemRepository());
		this.addPartRepository(this.getNodeRepository());
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
