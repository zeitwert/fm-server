package io.zeitwert.fm.test.model.impl;

import static io.zeitwert.ddd.util.Check.requireThis;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.jooq.DSLContext;
import org.jooq.JSON;
import org.jooq.exception.NoDataFoundException;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import io.zeitwert.ddd.persistence.jooq.AggregateState;
import io.zeitwert.ddd.persistence.jooq.base.ObjExtnPersistenceProviderBase;
import io.zeitwert.fm.account.model.db.tables.CodeCountry;
import io.zeitwert.fm.test.model.ObjTest;
import io.zeitwert.fm.test.model.ObjTestPartNode;
import io.zeitwert.fm.test.model.base.ObjTestBase;
import io.zeitwert.fm.test.model.ObjTestRepository;
import io.zeitwert.fm.test.model.db.Tables;
import io.zeitwert.fm.test.model.db.tables.records.ObjTestRecord;

@Configuration("objTestPersistenceProvider")
@DependsOn("codePartListTypeEnum")
public class ObjTestPersistenceProvider extends ObjExtnPersistenceProviderBase<ObjTest> {

	public ObjTestPersistenceProvider(DSLContext dslContext) {
		super(ObjTestRepository.class, ObjTestBase.class, dslContext);
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
	public Class<?> getEntityClass() {
		return ObjTest.class;
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

}
