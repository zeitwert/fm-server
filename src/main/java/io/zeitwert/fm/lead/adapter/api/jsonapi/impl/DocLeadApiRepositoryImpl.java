
package io.zeitwert.fm.lead.adapter.api.jsonapi.impl;

import org.springframework.stereotype.Controller;

import io.zeitwert.ddd.aggregate.adapter.api.jsonapi.base.AggregateApiRepositoryBase;
import io.zeitwert.ddd.session.model.RequestContext;
import io.zeitwert.fm.lead.adapter.api.jsonapi.DocLeadApiRepository;
import io.zeitwert.fm.lead.adapter.api.jsonapi.dto.DocLeadDto;
import io.zeitwert.fm.lead.model.DocLead;
import io.zeitwert.fm.lead.model.DocLeadRepository;
import io.zeitwert.fm.lead.model.db.tables.records.DocLeadVRecord;

@Controller("docLeadApiRepository")
public class DocLeadApiRepositoryImpl extends AggregateApiRepositoryBase<DocLead, DocLeadVRecord, DocLeadDto>
		implements DocLeadApiRepository {

	public DocLeadApiRepositoryImpl(final DocLeadRepository repository, RequestContext requestCtx) {
		super(DocLeadDto.class, requestCtx, repository, DocLeadDtoAdapter.getInstance());
	}

}
