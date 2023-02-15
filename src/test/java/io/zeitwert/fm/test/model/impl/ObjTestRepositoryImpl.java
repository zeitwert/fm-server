
package io.zeitwert.fm.test.model.impl;

import static io.dddrive.util.Invariant.requireThis;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.jooq.JSON;
import org.jooq.exception.NoDataFoundException;
import org.springframework.stereotype.Component;

import io.crnk.core.queryspec.QuerySpec;
import io.dddrive.jooq.ddd.AggregateState;
import io.dddrive.oe.model.enums.CodeCountry;
import io.zeitwert.fm.obj.model.base.FMObjRepositoryBase;
import io.zeitwert.fm.test.model.ObjTest;
import io.zeitwert.fm.test.model.ObjTestPartNode;
import io.zeitwert.fm.test.model.ObjTestRepository;
import io.zeitwert.fm.test.model.base.ObjTestBase;
import io.zeitwert.fm.test.model.db.Tables;
import io.zeitwert.fm.test.model.db.tables.records.ObjTestRecord;
import io.zeitwert.fm.test.model.db.tables.records.ObjTestVRecord;

@Component("objTestRepository")
public class ObjTestRepositoryImpl extends FMObjRepositoryBase<ObjTest, ObjTestVRecord>
		implements ObjTestRepository {

	private static final String AGGREGATE_TYPE = "obj_test";

	protected ObjTestRepositoryImpl() {
		super(ObjTestRepository.class, ObjTest.class, ObjTestBase.class, AGGREGATE_TYPE);
	}

	@Override
	public void mapProperties() {
		super.mapProperties();
		this.mapField("shortText", AggregateState.EXTN, "short_text", String.class);
		this.mapField("longText", AggregateState.EXTN, "long_text", String.class);
		this.mapField("date", AggregateState.EXTN, "date", LocalDate.class);
		this.mapField("int", AggregateState.EXTN, "int", Integer.class);
		this.mapField("isDone", AggregateState.EXTN, "is_done", Boolean.class);
		this.mapField("json", AggregateState.EXTN, "json", JSON.class);
		this.mapField("nr", AggregateState.EXTN, "nr", BigDecimal.class);
		this.mapField("country", AggregateState.EXTN, "country_id", String.class);
		this.mapField("refTest", AggregateState.EXTN, "ref_test_id", Integer.class);
		this.mapCollection("countrySet", "test.countrySet", CodeCountry.class);
		this.mapCollection("nodeList", "test.nodeList", ObjTestPartNode.class);
	}

	@Override
	public void registerPartRepositories() {
		super.registerPartRepositories();
		this.addPartRepository(this.getNodeRepository());
	}

	@Override
	public ObjTest doCreate() {
		return this.doCreate(this.dslContext().newRecord(Tables.OBJ_TEST));
	}

	@Override
	public ObjTest doLoad(Integer objId) {
		requireThis(objId != null, "objId not null");
		ObjTestRecord testRecord = this.dslContext().fetchOne(Tables.OBJ_TEST, Tables.OBJ_TEST.OBJ_ID.eq(objId));
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
