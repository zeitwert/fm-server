
package io.zeitwert.fm.test.model.impl;

import static io.zeitwert.ddd.util.Check.requireThis;

import java.util.List;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.jooq.exception.NoDataFoundException;
import org.springframework.stereotype.Component;

import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.property.model.enums.CodePartListType;
import io.zeitwert.ddd.property.model.enums.CodePartListTypeEnum;
import io.zeitwert.fm.doc.model.base.FMDocRepositoryBase;
import io.zeitwert.fm.test.model.DocTest;
import io.zeitwert.fm.test.model.DocTestRepository;
import io.zeitwert.fm.test.model.base.DocTestBase;
import io.zeitwert.fm.test.model.base.DocTestFields;
import io.zeitwert.fm.test.model.db.Tables;
import io.zeitwert.fm.test.model.db.tables.records.DocTestRecord;
import io.zeitwert.fm.test.model.db.tables.records.DocTestVRecord;

@Component("docTestRepository")
public class DocTestRepositoryImpl extends FMDocRepositoryBase<DocTest, DocTestVRecord> implements DocTestRepository {

	private static final String AGGREGATE_TYPE = "doc_test";

	private CodePartListType countrySetType;
	// private DocTestPartNodeRepository nodeRepository;
	// private CodePartListType nodeListType;

	protected DocTestRepositoryImpl(
			final AppContext appContext,
			final DSLContext dslContext) {
		super(
				DocTestRepository.class,
				DocTest.class,
				DocTestBase.class,
				AGGREGATE_TYPE,
				appContext,
				dslContext);
	}

	@Override
	@PostConstruct
	public void registerPartRepositories() {
		super.registerPartRepositories();
		this.addPartRepository(this.getItemRepository());
		// this.addPartRepository(this.getNodeRepository());
	}

	@Override
	public CodePartListType getCountrySetType() {
		if (this.countrySetType == null) {
			this.countrySetType = CodePartListTypeEnum.getPartListType(DocTestFields.COUNTRY_SET);
		}
		return this.countrySetType;
	}

	// @Override
	// public DocTestPartNodeRepository getNodeRepository() {
	// return this.nodeRepository;
	// }

	// @Override
	// public CodePartListType getNodeListType() {
	// return this.nodeListType;
	// }

	@Override
	public DocTest doCreate() {
		return this.doCreate(this.getDSLContext().newRecord(Tables.DOC_TEST));
	}

	@Override
	public DocTest doLoad(Integer docId) {
		requireThis(docId != null, "docId not null");
		DocTestRecord testRecord = this.getDSLContext().fetchOne(Tables.DOC_TEST, Tables.DOC_TEST.DOC_ID.eq(docId));
		if (testRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + docId + "]");
		}
		return this.doLoad(docId, testRecord);
	}

	@Override
	public List<DocTestVRecord> doFind(QuerySpec querySpec) {
		return this.doFind(Tables.DOC_TEST_V, Tables.DOC_TEST_V.ID, querySpec);
	}

}
