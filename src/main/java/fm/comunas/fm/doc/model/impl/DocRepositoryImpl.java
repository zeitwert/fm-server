
package fm.comunas.fm.doc.model.impl;

import java.util.List;
import java.util.Optional;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import io.crnk.core.queryspec.QuerySpec;
import fm.comunas.fm.doc.model.DocRepository;
import fm.comunas.ddd.app.service.api.AppContext;
import fm.comunas.ddd.doc.model.Doc;
import fm.comunas.ddd.doc.model.DocPartTransitionRepository;
import fm.comunas.ddd.doc.model.base.DocBase;
import fm.comunas.ddd.doc.model.base.DocRepositoryBase;
import fm.comunas.ddd.doc.model.db.Tables;
import fm.comunas.ddd.doc.model.db.tables.records.DocRecord;
import fm.comunas.ddd.oe.model.ObjTenant;
import fm.comunas.ddd.oe.model.ObjUser;
import fm.comunas.ddd.session.model.SessionInfo;

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
	public Optional<Doc> doLoad(SessionInfo sessionInfo, Integer docId) {
		DocRecord docRecord = this.dslContext.fetchOne(Tables.DOC, Tables.DOC.ID.eq(docId));
		if (docRecord == null) {
			return Optional.empty();
		}
		return Optional.of(this.newAggregate(sessionInfo, docRecord, null));
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
