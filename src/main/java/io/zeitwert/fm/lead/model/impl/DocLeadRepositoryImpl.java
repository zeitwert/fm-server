
package io.zeitwert.fm.lead.model.impl;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.exception.NoDataFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.fm.doc.model.DocPartNoteRepository;
import io.zeitwert.fm.doc.model.base.FMDocRepositoryBase;
import io.zeitwert.fm.lead.model.DocLead;
import io.zeitwert.fm.lead.model.DocLeadRepository;
import io.zeitwert.fm.lead.model.base.DocLeadBase;
import io.zeitwert.fm.lead.model.db.Tables;
import io.zeitwert.fm.lead.model.db.tables.records.DocLeadRecord;
import io.zeitwert.fm.lead.model.db.tables.records.DocLeadVRecord;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.doc.model.DocPartTransitionRepository;
import io.zeitwert.ddd.session.model.SessionInfo;

@Component("docLeadDbRepository")
public class DocLeadRepositoryImpl extends FMDocRepositoryBase<DocLead, DocLeadVRecord> implements DocLeadRepository {

	private static final String ITEM_TYPE = "doc_lead";

	@Autowired
	//@formatter:off
	protected DocLeadRepositoryImpl(
		final AppContext appContext,
		final DSLContext dslContext,
		final DocPartTransitionRepository transitionRepository,
		final DocPartNoteRepository noteRepository
	) {
		super(
			DocLeadRepository.class,
			DocLead.class,
			DocLeadBase.class,
			ITEM_TYPE,
			appContext,
			dslContext,
			transitionRepository,
			noteRepository
		);
	}
	//@formatter:on

	@Override
	public DocLead doCreate(SessionInfo sessionInfo) {
		return this.doCreate(sessionInfo, this.getDSLContext().newRecord(Tables.DOC_LEAD));
	}

	@Override
	public void doInitParts(DocLead doc) {
		super.doInitParts(doc);
	}

	@Override
	public DocLead doLoad(SessionInfo sessionInfo, Integer docId) {
		require(docId != null, "docId not null");
		DocLeadRecord leadRecord = this.getDSLContext().fetchOne(Tables.DOC_LEAD, Tables.DOC_LEAD.DOC_ID.eq(docId));
		if (leadRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + docId + "]");
		}
		return this.doLoad(sessionInfo, docId, leadRecord);
	}

	@Override
	public void doLoadParts(DocLead doc) {
		super.doLoadParts(doc);
		// Set<CodeArea> areaSet = this.getUtil().loadEnumSet(this.getDSLContext(),
		// doc.getId(), "", CodeAreaEnum.class);
		// ((DocLeadBase) doc).loadAreaSet(areaSet);
	}

	@Override
	public List<DocLeadVRecord> doFind(QuerySpec querySpec) {
		return this.doFind(Tables.DOC_LEAD_V, Tables.DOC_LEAD_V.ID, querySpec);
	}

}
