package io.zeitwert.fm.test.model.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.JSON;
import org.jooq.Result;
import org.springframework.stereotype.Component;

import io.dddrive.app.service.api.AppContext;
import io.dddrive.jooq.ddd.PartState;
import io.dddrive.jooq.obj.JooqObjPartRepositoryBase;
import io.zeitwert.fm.test.model.ObjTest;
import io.zeitwert.fm.test.model.ObjTestPartNode;
import io.zeitwert.fm.test.model.ObjTestPartNodeRepository;
import io.zeitwert.fm.test.model.base.ObjTestPartNodeBase;
import io.zeitwert.fm.test.model.db.Tables;
import io.zeitwert.fm.test.model.db.tables.records.ObjTestPartNodeRecord;

@Component("testPartNodeRepository")
public class ObjTestPartNodeRepositoryImpl extends JooqObjPartRepositoryBase<ObjTest, ObjTestPartNode>
		implements ObjTestPartNodeRepository {

	private static final String PART_TYPE = "obj_test_part_node";

	protected ObjTestPartNodeRepositoryImpl(AppContext appContext, DSLContext dslContext) {
		super(ObjTest.class, ObjTestPartNode.class, ObjTestPartNodeBase.class, PART_TYPE, appContext, dslContext);
	}

	@Override
	public void mapProperties() {
		super.mapProperties();
		this.mapField("shortText", PartState.BASE, "short_text", String.class);
		this.mapField("longText", PartState.BASE, "long_text", String.class);
		this.mapField("date", PartState.BASE, "date", LocalDate.class);
		this.mapField("int", PartState.BASE, "int", Integer.class);
		this.mapField("isDone", PartState.BASE, "is_done", Boolean.class);
		this.mapField("json", PartState.BASE, "json", JSON.class);
		this.mapField("nr", PartState.BASE, "nr", BigDecimal.class);
		this.mapField("country", PartState.BASE, "country_id", String.class);
		this.mapField("refTest", PartState.BASE, "ref_obj_id", Integer.class);
	}

	@Override
	public ObjTestPartNode doCreate(ObjTest obj) {
		ObjTestPartNodeRecord dbRecord = this.dslContext().newRecord(Tables.OBJ_TEST_PART_NODE);
		return this.getRepositorySPI().newPart(obj, new PartState(dbRecord));
	}

	@Override
	public List<ObjTestPartNode> doLoad(ObjTest obj) {
		Result<ObjTestPartNodeRecord> dbRecords = this.dslContext()
				.selectFrom(Tables.OBJ_TEST_PART_NODE)
				.where(Tables.OBJ_TEST_PART_NODE.OBJ_ID.eq(obj.getId()))
				.orderBy(Tables.OBJ_TEST_PART_NODE.SEQ_NR)
				.fetchInto(Tables.OBJ_TEST_PART_NODE);
		return dbRecords.map(dbRecord -> this.getRepositorySPI().newPart(obj, new PartState(dbRecord)));
	}

}
