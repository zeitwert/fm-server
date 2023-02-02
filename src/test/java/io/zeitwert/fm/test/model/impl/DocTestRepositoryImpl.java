
package io.zeitwert.fm.test.model.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.fm.doc.model.base.FMDocRepositoryBase;
import io.zeitwert.fm.test.model.DocTest;
import io.zeitwert.fm.test.model.DocTestRepository;
import io.zeitwert.fm.test.model.base.DocTestBase;
import io.zeitwert.fm.test.model.db.Tables;
import io.zeitwert.fm.test.model.db.tables.records.DocTestVRecord;

@Component("docTestRepository")
public class DocTestRepositoryImpl extends FMDocRepositoryBase<DocTest, DocTestVRecord> implements DocTestRepository {

	private static final String AGGREGATE_TYPE = "doc_test";

	protected DocTestRepositoryImpl(AppContext appContext) {
		super(DocTestRepository.class, DocTest.class, DocTestBase.class, AGGREGATE_TYPE, appContext);
	}

	@Override
	@PostConstruct
	public void registerPartRepositories() {
		super.registerPartRepositories();
		this.addPartRepository(this.getItemRepository());
		// this.addPartRepository(this.getNodeRepository());
	}

	// @Override
	// public DocTestPartNodeRepository getNodeRepository() {
	// return this.nodeRepository;
	// }

	@Override
	public List<DocTestVRecord> doFind(QuerySpec querySpec) {
		return this.doFind(Tables.DOC_TEST_V, Tables.DOC_TEST_V.ID, querySpec);
	}

}
