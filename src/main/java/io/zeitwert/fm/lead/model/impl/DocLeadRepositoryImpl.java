
package io.zeitwert.fm.lead.model.impl;

import static io.zeitwert.ddd.util.Check.requireThis;

import java.util.List;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.jooq.exception.NoDataFoundException;
import org.springframework.stereotype.Component;

import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.doc.model.DocPartTransitionRepository;
import io.zeitwert.ddd.session.model.RequestContext;
import io.zeitwert.fm.collaboration.model.ObjNoteRepository;
import io.zeitwert.fm.doc.model.base.FMDocRepositoryBase;
import io.zeitwert.fm.lead.model.DocLead;
import io.zeitwert.fm.lead.model.DocLeadRepository;
import io.zeitwert.fm.lead.model.base.DocLeadBase;
import io.zeitwert.fm.lead.model.db.Tables;
import io.zeitwert.fm.lead.model.db.tables.records.DocLeadRecord;
import io.zeitwert.fm.lead.model.db.tables.records.DocLeadVRecord;

@Component("docLeadDbRepository")
public class DocLeadRepositoryImpl extends FMDocRepositoryBase<DocLead, DocLeadVRecord> implements DocLeadRepository {

	private static final String AGGREGATE_TYPE = "doc_lead";

	//@formatter:off
	protected DocLeadRepositoryImpl(
		final AppContext appContext,
		final DSLContext dslContext,
		final DocPartTransitionRepository transitionRepository,
		final ObjNoteRepository noteRepository
	) {
		super(
			DocLeadRepository.class,
			DocLead.class,
			DocLeadBase.class,
			AGGREGATE_TYPE,
			appContext,
			dslContext,
			transitionRepository,
			noteRepository
		);
	}
	//@formatter:on

	@Override
	@PostConstruct
	public void registerPartRepositories() {
		super.registerPartRepositories();
	}

	@Override
	public DocLead doCreate(RequestContext requestCtx) {
		return this.doCreate(requestCtx, this.getDSLContext().newRecord(Tables.DOC_LEAD));
	}

	@Override
	public DocLead doLoad(RequestContext requestCtx, Integer docId) {
		requireThis(docId != null, "docId not null");
		DocLeadRecord leadRecord = this.getDSLContext().fetchOne(Tables.DOC_LEAD, Tables.DOC_LEAD.DOC_ID.eq(docId));
		if (leadRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + docId + "]");
		}
		return this.doLoad(requestCtx, docId, leadRecord);
	}

	@Override
	public List<DocLeadVRecord> doFind(RequestContext requestCtx, QuerySpec querySpec) {
		return this.doFind(requestCtx, Tables.DOC_LEAD_V, Tables.DOC_LEAD_V.ID, querySpec);
	}

}
