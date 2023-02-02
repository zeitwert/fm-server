
package io.zeitwert.fm.test.model.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.JSON;
import org.jooq.Result;
import org.springframework.context.annotation.Configuration;

import io.zeitwert.fm.test.model.ObjTest;
import io.zeitwert.fm.test.model.ObjTestPartNode;
import io.zeitwert.fm.test.model.ObjTestPartNodeRepository;
import io.zeitwert.ddd.obj.model.base.ObjPartPersistenceProviderBase;
import io.zeitwert.fm.test.model.base.ObjTestPartNodeBase;
import io.zeitwert.fm.test.model.db.Tables;
import io.zeitwert.fm.test.model.db.tables.records.ObjTestPartNodeRecord;
import io.zeitwert.ddd.persistence.jooq.PartState;

@Configuration
public class ObjTestPartNodePersistenceProvider extends ObjPartPersistenceProviderBase<ObjTest, ObjTestPartNode> {

	public ObjTestPartNodePersistenceProvider(DSLContext dslContext) {
		super(ObjTest.class, ObjTestPartNodeRepository.class, ObjTestPartNodeBase.class, dslContext);
		this.mapField("shortText", BASE, "short_text", String.class);
		this.mapField("longText", BASE, "long_text", String.class);
		this.mapField("date", BASE, "date", LocalDate.class);
		this.mapField("int", BASE, "int", Integer.class);
		this.mapField("isDone", BASE, "is_done", Boolean.class);
		this.mapField("json", BASE, "json", JSON.class);
		this.mapField("nr", BASE, "nr", BigDecimal.class);
		this.mapField("country", BASE, "country_id", String.class);
		this.mapField("refTest", BASE, "ref_obj_id", Integer.class);
	}

	@Override
	public boolean isReal() {
		return true;
	}

	@Override
	public Class<?> getEntityClass() {
		return ObjTestPartNode.class;
	}

	@Override
	public ObjTestPartNode doCreate(ObjTest obj) {
		ObjTestPartNodeRecord dbRecord = this.getDSLContext().newRecord(Tables.OBJ_TEST_PART_NODE);
		return this.newPart(obj, new PartState(dbRecord));
	}

	@Override
	public List<ObjTestPartNode> doLoad(ObjTest obj) {
		Result<ObjTestPartNodeRecord> dbRecords = this.getDSLContext()
				.selectFrom(Tables.OBJ_TEST_PART_NODE)
				.where(Tables.OBJ_TEST_PART_NODE.OBJ_ID.eq(obj.getId()))
				.orderBy(Tables.OBJ_TEST_PART_NODE.SEQ_NR)
				.fetchInto(Tables.OBJ_TEST_PART_NODE);
		return dbRecords.map(dbRecord -> this.newPart(obj, new PartState(dbRecord)));
	}

}
