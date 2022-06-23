package io.zeitwert.fm.test.model.impl;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.obj.model.base.ObjPartRepositoryBase;
import io.zeitwert.fm.test.model.ObjTest;
import io.zeitwert.fm.test.model.ObjTestPartNode;
import io.zeitwert.fm.test.model.ObjTestPartNodeRepository;
import io.zeitwert.fm.test.model.base.ObjTestPartNodeBase;
import io.zeitwert.fm.test.model.db.Tables;
import io.zeitwert.fm.test.model.db.tables.records.ObjTestPartNodeRecord;

@Component("testPartNodeRepository")
public class ObjTestPartNodeRepositoryImpl extends ObjPartRepositoryBase<ObjTest, ObjTestPartNode>
		implements ObjTestPartNodeRepository {

	private static final String PART_TYPE = "obj_test_part_node";

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
	public ObjTestPartNode doCreate(ObjTest obj) {
		ObjTestPartNodeRecord dbRecord = this.getDSLContext().newRecord(Tables.OBJ_TEST_PART_NODE);
		return this.newPart(obj, dbRecord);
	}

	@Override
	public List<ObjTestPartNode> doLoad(ObjTest obj) {
		//@formatter:off
		Result<ObjTestPartNodeRecord> dbRecords = this.getDSLContext()
			.selectFrom(Tables.OBJ_TEST_PART_NODE)
			.where(Tables.OBJ_TEST_PART_NODE.OBJ_ID.eq(obj.getId()))
			.orderBy(Tables.OBJ_TEST_PART_NODE.SEQ_NR)
			.fetchInto(Tables.OBJ_TEST_PART_NODE);
		//@formatter:on
		return dbRecords.map(dbRecord -> this.newPart(obj, dbRecord));
	}

}
