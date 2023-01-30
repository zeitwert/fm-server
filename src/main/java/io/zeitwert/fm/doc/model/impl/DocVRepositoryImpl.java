
package io.zeitwert.fm.doc.model.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.jooq.exception.NoDataFoundException;
import org.springframework.stereotype.Component;

import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.base.DocRepositoryBase;
import io.zeitwert.fm.doc.model.db.Tables;
import io.zeitwert.fm.doc.model.db.tables.records.DocRecord;
import io.zeitwert.fm.doc.model.DocVRepository;
import io.zeitwert.fm.doc.model.base.DocVBase;

@Component("docRepository")
public class DocVRepositoryImpl extends DocRepositoryBase<Doc, DocRecord> implements DocVRepository {

	private static final String AGGREGATE_TYPE = "doc";

	protected DocVRepositoryImpl(
			final AppContext appContext,
			final DSLContext dslContext) {
		super(
				DocVRepository.class,
				Doc.class,
				DocVBase.class,
				AGGREGATE_TYPE,
				appContext,
				dslContext);
	}

	@Override
	@PostConstruct
	public void registerPartRepositories() {
		super.registerPartRepositories();
	}

	@Override
	public Doc doCreate() {
		throw new RuntimeException("DocV is readonly");
	}

	@Override
	public Doc doLoad(Integer docId) {
		DocRecord docRecord = this.getDSLContext().fetchOne(Tables.DOC, Tables.DOC.ID.eq(docId));
		if (docRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + docId + "]");
		}
		return this.newAggregate(docRecord, null);
	}

	@Override
	public List<DocRecord> doFind(QuerySpec querySpec) {
		return this.doFind(Tables.DOC, Tables.DOC.ID, querySpec);
	}

}
