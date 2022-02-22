package fm.comunas.fm.test.model.impl;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fm.comunas.ddd.app.service.api.AppContext;
import fm.comunas.ddd.obj.model.base.ObjPartRepositoryBase;
import fm.comunas.fm.test.model.ObjTest;
import fm.comunas.fm.test.model.ObjTestPartNode;
import fm.comunas.fm.test.model.ObjTestPartNodeRepository;
import fm.comunas.fm.test.model.base.ObjTestPartNodeBase;
import fm.comunas.fm.test.model.db.Tables;
import fm.comunas.fm.test.model.db.tables.records.ObjTestPartNodeRecord;

import java.util.List;

@Component("testPartNodeRepository")
public class ObjTestPartNodeRepositoryImpl extends ObjPartRepositoryBase<ObjTest, ObjTestPartNode>
		implements ObjTestPartNodeRepository {

	private static final String PART_TYPE = "obj_test_part_node";

	@Autowired
	//@formatter:off
	protected ObjTestPartNodeRepositoryImpl(
		final AppContext appContext,
		final DSLContext dslContext
	) {
		super(
			ObjTest.class,
			ObjTestPartNode.class,
			ObjTestPartNodeBase.class,
			PART_TYPE,
			appContext,
			dslContext
		);
	}
	//@formatter:on

	@Override
	public List<ObjTestPartNode> doLoad(ObjTest obj) {
		//@formatter:off
		Result<ObjTestPartNodeRecord> dbRecords = this.dslContext
			.selectFrom(Tables.OBJ_TEST_PART_NODE)
			.where(Tables.OBJ_TEST_PART_NODE.OBJ_ID.eq(obj.getId()))
			.orderBy(Tables.OBJ_TEST_PART_NODE.SEQ_NR)
			.fetchInto(Tables.OBJ_TEST_PART_NODE);
		//@formatter:on
		return dbRecords.map(dbRecord -> this.newPart(obj, dbRecord));
	}

	@Override
	public ObjTestPartNode doCreate(ObjTest obj) {
		ObjTestPartNodeRecord dbRecord = this.dslContext.newRecord(Tables.OBJ_TEST_PART_NODE);
		return this.newPart(obj, dbRecord);
	}

}
