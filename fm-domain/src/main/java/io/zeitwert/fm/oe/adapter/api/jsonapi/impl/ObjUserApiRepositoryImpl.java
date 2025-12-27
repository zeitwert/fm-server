package io.zeitwert.fm.oe.adapter.api.jsonapi.impl;

import io.zeitwert.dddrive.app.model.RequestContext;
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base.AggregateApiRepositoryBase;
import io.zeitwert.fm.oe.adapter.api.jsonapi.ObjUserApiRepository;
import io.zeitwert.fm.oe.adapter.api.jsonapi.dto.ObjUserDto;
import io.zeitwert.fm.oe.model.ObjUser;
import io.zeitwert.fm.oe.model.ObjUserRepository;
import org.springframework.stereotype.Controller;

@Controller("objUserApiRepository")
public class ObjUserApiRepositoryImpl
		extends AggregateApiRepositoryBase<ObjUser, ObjUserDto>
		implements ObjUserApiRepository {

	public ObjUserApiRepositoryImpl(
			ObjUserRepository repository,
			RequestContext requestCtx,
			ObjUserRepository userRepository,
			ObjUserDtoAdapter dtoAdapter) {
		super(ObjUserDto.class, requestCtx, userRepository, repository, dtoAdapter);
	}

}
