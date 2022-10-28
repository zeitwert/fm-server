
package io.zeitwert.fm.doc.model.impl;

import static io.zeitwert.ddd.util.Check.assertThis;

import java.util.List;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.jooq.exception.NoDataFoundException;
import org.springframework.stereotype.Component;

import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.DocPartTransitionRepository;
import io.zeitwert.ddd.doc.model.base.DocBase;
import io.zeitwert.ddd.doc.model.base.DocRepositoryBase;
import io.zeitwert.ddd.doc.model.db.Tables;
import io.zeitwert.ddd.doc.model.db.tables.records.DocRecord;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.fm.doc.model.DocRepository;

@Component("docRepository")
public class DocRepositoryImpl extends DocRepositoryBase<Doc, DocRecord> implements DocRepository {

	private static final String AGGREGATE_TYPE = "doc";

	//@formatter:off
	protected DocRepositoryImpl(
		final AppContext appContext,
		final DSLContext dslContext,
		final DocPartTransitionRepository transitionRepository
	) {
		super(
			DocRepository.class,
			Doc.class,
			DocBase.class,
			AGGREGATE_TYPE,
			appContext,
			dslContext,
			transitionRepository
		);
	}
	//@formatter:on

	@Override
	@PostConstruct
	public void registerPartRepositories() {
		super.registerPartRepositories();
	}

	@Override
	public DocBase doCreate() {
		assertThis(false, "cannot create a doc");
		return null;
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

	@Override
	public void changeOwner(List<Doc> docs, ObjUser user) {
		// docs.stream().forEach((doc) -> {
		// Doc instance = ((DocBase) doc).getInstance();
		// instance.setOwner(user);
		// this.store(instance);
		// });
	}

	// @Override
	// public Integer getDocumentCount(Item item) {
	// 	//@formatter:off
	// 	return this.getDSLContext().fetchCount(
	// 		this.dslContext
	// 			.selectFrom(Tables.DOC_PART_LIST)
	// 			.where(Tables.DOC_PART_LIST.DOC_ID.eq(item.getId()))
	// 			.and(Tables.DOC_PART_LIST.PART_LIST_TYPE_ID.eq(ITEM_LIST_DOCUMENT))
	// 	);
	// 	//@formatter:on
	// }

}
