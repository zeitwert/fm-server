
package io.zeitwert.fm.test.model.impl;

import static io.dddrive.util.Invariant.requireThis;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.exception.NoDataFoundException;
import org.springframework.stereotype.Component;

import io.crnk.core.queryspec.QuerySpec;
import io.dddrive.app.service.api.AppContext;
import io.dddrive.jooq.ddd.AggregateState;
import io.dddrive.oe.model.enums.CodeCountry;
import io.zeitwert.fm.doc.model.base.FMDocExtnRepositoryBase;
import io.zeitwert.fm.test.model.DocTest;
import io.zeitwert.fm.test.model.DocTestPartNode;
import io.zeitwert.fm.test.model.DocTestRepository;
import io.zeitwert.fm.test.model.base.DocTestBase;
import io.zeitwert.fm.test.model.db.Tables;
import io.zeitwert.fm.test.model.db.tables.records.DocTestRecord;
import io.zeitwert.fm.test.model.db.tables.records.DocTestVRecord;

@Component("docTestRepository")
public class DocTestRepositoryImpl extends FMDocExtnRepositoryBase<DocTest, DocTestVRecord>
		implements DocTestRepository {

	private static final String AGGREGATE_TYPE = "doc_test";

	protected DocTestRepositoryImpl(AppContext appContext, DSLContext dslContext) {
		super(DocTestRepository.class, DocTest.class, DocTestBase.class, AGGREGATE_TYPE, appContext, dslContext);
	}

	@Override
	public void mapProperties() {
		super.mapProperties();
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

	@Override
	public List<DocTestVRecord> doFind(QuerySpec querySpec) {
		return this.doFind(Tables.DOC_TEST_V, Tables.DOC_TEST_V.ID, querySpec);
	}

}
