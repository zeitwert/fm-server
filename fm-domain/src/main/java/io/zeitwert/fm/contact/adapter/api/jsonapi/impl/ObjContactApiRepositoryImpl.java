package io.zeitwert.fm.contact.adapter.api.jsonapi.impl;

import io.zeitwert.dddrive.app.model.RequestContext;
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base.AggregateApiRepositoryBase;
import io.zeitwert.fm.contact.adapter.api.jsonapi.ObjContactApiRepository;
import io.zeitwert.fm.contact.adapter.api.jsonapi.dto.ObjContactDto;
import io.zeitwert.fm.contact.model.ObjContact;
import io.zeitwert.fm.contact.model.ObjContactRepository;
import io.zeitwert.fm.oe.model.ObjUserRepository;
import org.springframework.stereotype.Controller;

@Controller("objContactApiRepository")
public class ObjContactApiRepositoryImpl
		extends AggregateApiRepositoryBase<ObjContact, ObjContactDto>
		implements ObjContactApiRepository {

	public ObjContactApiRepositoryImpl(
			ObjContactRepository repository,
			RequestContext requestCtx,
			ObjUserRepository userRepository,
			ObjContactDtoAdapter dtoAdapter) {
		super(ObjContactDto.class, requestCtx, userRepository, repository, dtoAdapter);
	}

}
