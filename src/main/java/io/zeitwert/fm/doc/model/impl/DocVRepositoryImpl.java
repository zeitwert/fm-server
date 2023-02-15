
package io.zeitwert.fm.doc.model.impl;

import static io.dddrive.util.Invariant.assertThis;
import static io.dddrive.util.Invariant.requireThis;

import java.util.List;

import org.springframework.stereotype.Component;

import io.crnk.core.queryspec.QuerySpec;
import io.dddrive.doc.model.Doc;
import io.dddrive.jooq.ddd.AggregateState;
import io.dddrive.jooq.doc.JooqDocRepositoryBase;
import io.zeitwert.fm.doc.model.db.Tables;
import io.zeitwert.fm.doc.model.db.tables.records.DocRecord;
import io.zeitwert.fm.app.model.RequestContextFM;
import io.zeitwert.fm.ddd.model.base.AggregateFindMixin;
import io.zeitwert.fm.doc.model.DocVRepository;
import io.zeitwert.fm.doc.model.base.DocPersistenceProviderMixin;
import io.zeitwert.fm.doc.model.base.DocVBase;

@Component("docRepository")
public class DocVRepositoryImpl extends JooqDocRepositoryBase<Doc, DocRecord>
		implements DocVRepository, DocPersistenceProviderMixin<Doc>, AggregateFindMixin<DocRecord> {

	private static final String AGGREGATE_TYPE = "doc";

	protected DocVRepositoryImpl() {
		super(DocVRepository.class, Doc.class, DocVBase.class, AGGREGATE_TYPE);
	}

	@Override
	public void mapProperties() {
		super.mapProperties();
		this.mapField("accountId", AggregateState.BASE, "account_id", Integer.class);
	}

	@Override
	public boolean hasAccount() {
		return true;
	}

	@Override
	public Doc doCreate() {
		assertThis(false, "not supported");
		return null;
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

	@Override
	public final List<DocRecord> find(QuerySpec querySpec) {
		return this.doFind(this.queryWithFilter(querySpec, (RequestContextFM) this.getAppContext().getRequestContext()));
	}

}
