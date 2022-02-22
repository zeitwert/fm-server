
package io.zeitwert.fm.lead.model.impl;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.fm.common.model.enums.CodeArea;
import io.zeitwert.fm.common.model.enums.CodeAreaEnum;
import io.zeitwert.fm.doc.model.DocPartNoteRepository;
import io.zeitwert.fm.doc.model.base.FMDocRepositoryBase;
import io.zeitwert.fm.lead.model.DocLead;
import io.zeitwert.fm.lead.model.DocLeadRepository;
import io.zeitwert.fm.lead.model.base.DocLeadBase;
import io.zeitwert.fm.lead.model.db.Tables;
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
		return doCreate(sessionInfo, this.dslContext.newRecord(Tables.DOC_LEAD));
	}

	@Override
	public void doInitParts(DocLead obj) {
		super.doInitParts(obj);
	}

	@Override
	public List<DocLeadVRecord> doFind(QuerySpec querySpec) {
		return this.doFind(Tables.DOC_LEAD_V, Tables.DOC_LEAD_V.ID, querySpec);
	}

	@Override
	public Optional<DocLead> doLoad(SessionInfo sessionInfo, Integer objId) {
		require(objId != null, "objId not null");
		return this.doLoad(sessionInfo, objId, this.dslContext.fetchOne(Tables.DOC_LEAD, Tables.DOC_LEAD.DOC_ID.eq(objId)));
	}

	@Override
	public void doLoadParts(DocLead obj) {
		super.doLoadParts(obj);
		Set<CodeArea> areaSet = this.getUtil().loadEnumSet(this.dslContext, obj.getId(), "", CodeAreaEnum.class);
		((DocLeadBase) obj).loadAreaSet(areaSet);
	}

}
