package io.zeitwert.fm.test.model.impl;

import static io.zeitwert.ddd.util.Check.requireThis;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.jooq.DSLContext;
import org.jooq.exception.NoDataFoundException;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import io.zeitwert.ddd.persistence.jooq.base.DocExtnPersistenceProviderBase;
import io.zeitwert.fm.account.model.enums.CodeCountry;
import io.zeitwert.fm.test.model.DocTest;
import io.zeitwert.fm.test.model.DocTestPartNode;
import io.zeitwert.fm.test.model.DocTestRepository;
import io.zeitwert.fm.test.model.base.DocTestBase;
import io.zeitwert.fm.test.model.db.Tables;
import io.zeitwert.fm.test.model.db.tables.records.DocTestRecord;

@Configuration("docTestPersistenceProvider")
@DependsOn("codePartListTypeEnum")
public class DocTestPersistenceProvider extends DocExtnPersistenceProviderBase<DocTest> {

	public DocTestPersistenceProvider(DSLContext dslContext) {
		super(DocTestRepository.class, DocTestBase.class, dslContext);
		this.mapField("shortText", EXTN, "short_text", String.class);
		this.mapField("longText", EXTN, "long_text", String.class);
		this.mapField("date", EXTN, "date", LocalDate.class);
		this.mapField("int", EXTN, "int", Integer.class);
		this.mapField("isDone", EXTN, "is_done", Boolean.class);
		this.mapField("json", EXTN, "json", org.jooq.JSON.class);
		this.mapField("nr", EXTN, "nr", BigDecimal.class);
		this.mapField("country", EXTN, "country_id", String.class);
		this.mapField("refObj", EXTN, "ref_obj_id", Integer.class);
		this.mapField("refDoc", EXTN, "ref_doc_id", Integer.class);
		this.mapCollection("countrySet", "test.countrySet", CodeCountry.class);
		this.mapCollection("nodeList", "test.nodeList", DocTestPartNode.class);
	}

	@Override
	public Class<?> getEntityClass() {
		return DocTest.class;
	}

	@Override
	public DocTest doCreate() {
		return this.doCreate(this.getDSLContext().newRecord(Tables.DOC_TEST));
	}

	@Override
	public DocTest doLoad(Integer objId) {
		requireThis(objId != null, "objId not null");
		DocTestRecord testRecord = this.getDSLContext().fetchOne(Tables.DOC_TEST, Tables.DOC_TEST.DOC_ID.eq(objId));
		if (testRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + objId + "]");
		}
		return this.doLoad(objId, testRecord);
	}

}
