
package io.zeitwert.fm.lead.adapter.api.jsonapi.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.zeitwert.fm.lead.adapter.api.jsonapi.DocLeadApiRepository;
import io.zeitwert.fm.lead.adapter.api.jsonapi.dto.DocLeadDto;
import io.zeitwert.fm.lead.model.DocLead;
import io.zeitwert.fm.lead.model.DocLeadRepository;
import io.zeitwert.fm.lead.model.db.tables.records.DocLeadVRecord;
import io.zeitwert.ddd.aggregate.adapter.api.jsonapi.base.AggregateApiAdapter;
import io.zeitwert.ddd.session.model.SessionInfo;

@Controller("docLeadApiRepository")
public class DocLeadApiRepositoryImpl extends AggregateApiAdapter<DocLead, DocLeadVRecord, DocLeadDto>
		implements DocLeadApiRepository {

	@Autowired
	public DocLeadApiRepositoryImpl(final DocLeadRepository repository, SessionInfo sessionInfo) {
		super(DocLeadDto.class, sessionInfo, repository, DocLeadDtoBridge.getInstance());
	}

}
