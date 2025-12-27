package io.zeitwert.fm.dms.adapter.api.jsonapi.impl;

import io.zeitwert.dddrive.app.model.RequestContext;
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base.AggregateApiRepositoryBase;
import io.zeitwert.fm.dms.adapter.api.jsonapi.ObjDocumentApiRepository;
import io.zeitwert.fm.dms.adapter.api.jsonapi.dto.ObjDocumentDto;
import io.zeitwert.fm.dms.model.ObjDocument;
import io.zeitwert.fm.dms.model.ObjDocumentRepository;
import io.zeitwert.fm.oe.model.ObjUserRepository;
import org.springframework.stereotype.Controller;

@Controller("objDocumentApiRepository")
public class ObjDocumentApiRepositoryImpl
		extends AggregateApiRepositoryBase<ObjDocument, ObjDocumentDto>
		implements ObjDocumentApiRepository {

	public ObjDocumentApiRepositoryImpl(
			ObjDocumentRepository repository,
			RequestContext requestCtx,
			ObjUserRepository userRepository,
			ObjDocumentDtoAdapter dtoAdapter) {
		super(ObjDocumentDto.class, requestCtx, userRepository, repository, dtoAdapter);
	}

}
