
package io.zeitwert.fm.dms.adapter.api.jsonapi.impl;

import org.springframework.stereotype.Controller;

import io.zeitwert.ddd.aggregate.adapter.api.jsonapi.base.AggregateApiAdapter;
import io.zeitwert.ddd.session.model.SessionInfo;
import io.zeitwert.fm.dms.adapter.api.jsonapi.ObjDocumentApiRepository;
import io.zeitwert.fm.dms.adapter.api.jsonapi.dto.ObjDocumentDto;
import io.zeitwert.fm.dms.model.ObjDocument;
import io.zeitwert.fm.dms.model.ObjDocumentRepository;
import io.zeitwert.fm.dms.model.db.tables.records.ObjDocumentVRecord;

@Controller("objDocumentApiRepository")
public class ObjDocumentApiRepositoryImpl extends AggregateApiAdapter<ObjDocument, ObjDocumentVRecord, ObjDocumentDto>
		implements ObjDocumentApiRepository {

	public ObjDocumentApiRepositoryImpl(final ObjDocumentRepository repository, SessionInfo sessionInfo) {
		super(ObjDocumentDto.class, sessionInfo, repository, ObjDocumentDtoBridge.getInstance());
	}

}
