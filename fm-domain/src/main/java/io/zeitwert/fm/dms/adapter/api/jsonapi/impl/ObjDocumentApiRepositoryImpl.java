
package io.zeitwert.fm.dms.adapter.api.jsonapi.impl;

import org.springframework.stereotype.Controller;

import io.zeitwert.dddrive.app.model.RequestContext;
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base.AggregateApiRepositoryBase;
import io.zeitwert.fm.dms.adapter.api.jsonapi.ObjDocumentApiRepository;
import io.zeitwert.fm.dms.adapter.api.jsonapi.dto.ObjDocumentDto;
import io.zeitwert.fm.dms.model.ObjDocument;
import io.zeitwert.fm.dms.model.ObjDocumentRepository;
import io.zeitwert.fm.oe.model.ObjUserFMRepository;

@Controller("objDocumentApiRepository")
public class ObjDocumentApiRepositoryImpl
		extends AggregateApiRepositoryBase<ObjDocument, ObjDocumentDto>
		implements ObjDocumentApiRepository {

	public ObjDocumentApiRepositoryImpl(
			ObjDocumentRepository repository,
			RequestContext requestCtx,
			ObjUserFMRepository userRepository,
			ObjDocumentDtoAdapter dtoAdapter) {
		super(ObjDocumentDto.class, requestCtx, userRepository, repository, dtoAdapter);
	}

}
