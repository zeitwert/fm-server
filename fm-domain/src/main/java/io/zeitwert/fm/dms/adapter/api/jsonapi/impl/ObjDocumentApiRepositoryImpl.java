
package io.zeitwert.fm.dms.adapter.api.jsonapi.impl;

import org.springframework.stereotype.Controller;

import io.dddrive.app.model.RequestContext;
import io.dddrive.ddd.adapter.api.jsonapi.base.AggregateApiRepositoryBase;
import io.dddrive.oe.service.api.ObjUserCache;
import io.zeitwert.fm.dms.adapter.api.jsonapi.ObjDocumentApiRepository;
import io.zeitwert.fm.dms.adapter.api.jsonapi.dto.ObjDocumentDto;
import io.zeitwert.fm.dms.model.ObjDocument;
import io.zeitwert.fm.dms.model.ObjDocumentRepository;
import io.zeitwert.fm.dms.model.db.tables.records.ObjDocumentVRecord;

@Controller("objDocumentApiRepository")
public class ObjDocumentApiRepositoryImpl
		extends AggregateApiRepositoryBase<ObjDocument, ObjDocumentVRecord, ObjDocumentDto>
		implements ObjDocumentApiRepository {

	public ObjDocumentApiRepositoryImpl(
			ObjDocumentRepository repository,
			RequestContext requestCtx,
			ObjUserCache userCache,
			ObjDocumentDtoAdapter dtoAdapter) {
		super(ObjDocumentDto.class, requestCtx, userCache, repository, dtoAdapter);
	}

}
