
package io.zeitwert.fm.doc.model.impl;

import static io.zeitwert.ddd.util.Check.requireThis;

import java.util.List;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.fm.doc.model.db.Tables;
import io.zeitwert.fm.doc.model.db.tables.records.DocRecord;
import io.zeitwert.jooq.repository.JooqDocRepositoryBase;
import io.zeitwert.fm.doc.model.DocVRepository;
import io.zeitwert.fm.doc.model.base.DocVBase;

@Component("docRepository")
public class DocVRepositoryImpl extends JooqDocRepositoryBase<Doc, DocRecord> implements DocVRepository {

	private static final String AGGREGATE_TYPE = "doc";

	protected DocVRepositoryImpl(AppContext appContext, DSLContext dslContext) {
		super(DocVRepository.class, Doc.class, DocVBase.class, AGGREGATE_TYPE, appContext, dslContext);
	}

	@Override
	@PostConstruct
	public void registerPartRepositories() {
		super.registerPartRepositories();
	}

	@Override
	public Doc doCreate() {
		return this.doCreate(null);
	}

	@Override
	public Doc doLoad(Integer docId) {
		requireThis(docId != null, "docId not null");
		return this.doLoad(docId, null);
	}

	@Override
	public List<DocRecord> doFind(QuerySpec querySpec) {
		return this.doFind(Tables.DOC, Tables.DOC.ID, querySpec);
	}

}
