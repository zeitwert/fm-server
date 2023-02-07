package io.zeitwert.fm.test.model.impl;

import static io.zeitwert.ddd.util.Check.requireThis;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.jooq.DSLContext;
import org.jooq.exception.NoDataFoundException;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import io.zeitwert.fm.account.model.enums.CodeCountry;
import io.zeitwert.fm.test.model.DocTest;
import io.zeitwert.fm.test.model.DocTestPartNode;
import io.zeitwert.fm.test.model.db.Tables;
import io.zeitwert.fm.test.model.db.tables.records.DocTestRecord;
import io.zeitwert.jooq.persistence.AggregateState;
import io.zeitwert.jooq.persistence.DocExtnPersistenceProviderBase;

@Configuration("docTestPersistenceProvider")
@DependsOn("codePartListTypeEnum")
public class DocTestPersistenceProvider extends DocExtnPersistenceProviderBase<DocTest> {

	public DocTestPersistenceProvider(DSLContext dslContext) {
		super(DocTest.class, dslContext);
		this.mapField("shortText", AggregateState.EXTN, "short_text", String.class);
		this.mapField("longText", AggregateState.EXTN, "long_text", String.class);
		this.mapField("date", AggregateState.EXTN, "date", LocalDate.class);
		this.mapField("int", AggregateState.EXTN, "int", Integer.class);
		this.mapField("isDone", AggregateState.EXTN, "is_done", Boolean.class);
		this.mapField("json", AggregateState.EXTN, "json", org.jooq.JSON.class);
		this.mapField("nr", AggregateState.EXTN, "nr", BigDecimal.class);
		this.mapField("country", AggregateState.EXTN, "country_id", String.class);
		this.mapField("refObj", AggregateState.EXTN, "ref_obj_id", Integer.class);
		this.mapField("refDoc", AggregateState.EXTN, "ref_doc_id", Integer.class);
		this.mapCollection("countrySet", "test.countrySet", CodeCountry.class);
		this.mapCollection("nodeList", "test.nodeList", DocTestPartNode.class);
	}

	@Override
	public DocTest doCreate() {
		return this.doCreate(this.dslContext().newRecord(Tables.DOC_TEST));
	}

	@Override
	public DocTest doLoad(Integer objId) {
		requireThis(objId != null, "objId not null");
		DocTestRecord testRecord = this.dslContext().fetchOne(Tables.DOC_TEST, Tables.DOC_TEST.DOC_ID.eq(objId));
		if (testRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + objId + "]");
		}
		return this.doLoad(objId, testRecord);
	}

}
