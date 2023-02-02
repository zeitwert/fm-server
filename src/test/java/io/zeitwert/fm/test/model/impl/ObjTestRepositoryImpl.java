
package io.zeitwert.fm.test.model.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.fm.obj.model.base.FMObjRepositoryBase;
import io.zeitwert.fm.test.model.ObjTest;
import io.zeitwert.fm.test.model.ObjTestPartNodeRepository;
import io.zeitwert.fm.test.model.ObjTestRepository;
import io.zeitwert.fm.test.model.base.ObjTestBase;
import io.zeitwert.fm.test.model.db.Tables;
import io.zeitwert.fm.test.model.db.tables.records.ObjTestVRecord;

@Component("objTestRepository")
public class ObjTestRepositoryImpl extends FMObjRepositoryBase<ObjTest, ObjTestVRecord> implements ObjTestRepository {

	private static final String AGGREGATE_TYPE = "obj_test";

	private ObjTestPartNodeRepository nodeRepository;

	protected ObjTestRepositoryImpl(final AppContext appContext, final DSLContext dslContext) {
		super(ObjTestRepository.class, ObjTest.class, ObjTestBase.class, AGGREGATE_TYPE, appContext, dslContext);
	}

	@Override
	public ObjTestPartNodeRepository getNodeRepository() {
		if (this.nodeRepository == null) {
			this.nodeRepository = this.getAppContext().getBean(ObjTestPartNodeRepository.class);
		}
		return this.nodeRepository;
	}

	@Override
	@PostConstruct
	public void registerPartRepositories() {
		super.registerPartRepositories();
		this.addPartRepository(this.getItemRepository());
		this.addPartRepository(this.getNodeRepository());
	}

	@Override
	public List<ObjTestVRecord> doFind(QuerySpec querySpec) {
		return this.doFind(Tables.OBJ_TEST_V, Tables.OBJ_TEST_V.ID, querySpec);
	}

}
