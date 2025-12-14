
package io.zeitwert.fm.contact.adapter.api.jsonapi.impl;

import org.springframework.stereotype.Controller;

import io.zeitwert.dddrive.app.model.RequestContext;
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base.AggregateApiRepositoryBase;
import io.zeitwert.fm.contact.adapter.api.jsonapi.ObjContactApiRepository;
import io.zeitwert.fm.contact.adapter.api.jsonapi.dto.ObjContactDto;
import io.zeitwert.fm.contact.model.ObjContact;
import io.zeitwert.fm.contact.model.ObjContactRepository;
import io.zeitwert.fm.oe.model.ObjUserFMRepository;

@Controller("objContactApiRepository")
public class ObjContactApiRepositoryImpl
		extends AggregateApiRepositoryBase<ObjContact, ObjContactDto>
		implements ObjContactApiRepository {

	public ObjContactApiRepositoryImpl(
			ObjContactRepository repository,
			RequestContext requestCtx,
			ObjUserFMRepository userRepository,
			ObjContactDtoAdapter dtoAdapter) {
		super(ObjContactDto.class, requestCtx, userRepository, repository, dtoAdapter);
	}

}
