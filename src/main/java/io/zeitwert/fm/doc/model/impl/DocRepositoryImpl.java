
package io.zeitwert.fm.doc.model.impl;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.exception.NoDataFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.fm.doc.model.DocRepository;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.DocPartTransitionRepository;
import io.zeitwert.ddd.doc.model.base.DocBase;
import io.zeitwert.ddd.doc.model.base.DocRepositoryBase;
import io.zeitwert.ddd.doc.model.db.Tables;
import io.zeitwert.ddd.doc.model.db.tables.records.DocRecord;
import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.session.model.SessionInfo;

@Component("docRepository")
public class DocRepositoryImpl extends DocRepositoryBase<Doc, DocRecord> implements DocRepository {

	private static final String ITEM_TYPE = "doc";

	@Autowired
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
			ITEM_TYPE,
			appContext,
			dslContext,
			transitionRepository
		);
	}
	//@formatter:on

	@Override
	public DocBase doCreate(SessionInfo sessionInfo) {
		Assert.isTrue(false, "cannot create a doc");
		return null;
	}

	@Override
	public void doInit(Doc doc, Integer docId, ObjTenant tenant, ObjUser user) {
		super.doInit(doc, docId, "advice", "advice.establish");
		// DocOpportunityRecord adviceRecord = ((DocAdviceBase) doc).getDbRecord();
		// adviceRecord.setDocId(docId);
	}

	@Override
	public Doc doLoad(SessionInfo sessionInfo, Integer docId) {
		DocRecord docRecord = this.dslContext.fetchOne(Tables.DOC, Tables.DOC.ID.eq(docId));
		if (docRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + docId + "]");
		}
		return this.newAggregate(sessionInfo, docRecord, null);
	}

	@Override
	public List<DocRecord> doFind(QuerySpec querySpec) {
		return this.doFind(Tables.DOC, Tables.DOC.ID, querySpec);
	}

	@Override
	public void changeOwner(List<Doc> docs, ObjUser user) {
		docs.stream().forEach((doc) -> {
			Doc instance = ((DocBase) doc).getInstance();
			instance.setOwner(user);
			this.store(instance);
		});
	}

	// @Override
	// public Integer getDocumentCount(Item item) {
	// 	//@formatter:off
	// 	return this.dslContext.fetchCount(
	// 		this.dslContext
	// 			.selectFrom(Tables.DOC_PART_LIST)
	// 			.where(Tables.DOC_PART_LIST.DOC_ID.eq(item.getId()))
	// 			.and(Tables.DOC_PART_LIST.PART_LIST_TYPE_ID.eq(ITEM_LIST_DOCUMENT))
	// 	);
	// 	//@formatter:on
	// }

}
