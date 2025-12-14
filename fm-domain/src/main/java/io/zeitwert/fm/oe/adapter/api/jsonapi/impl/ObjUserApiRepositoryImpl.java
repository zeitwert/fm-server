package io.zeitwert.fm.oe.adapter.api.jsonapi.impl;

import org.springframework.stereotype.Controller;

import io.zeitwert.dddrive.app.model.RequestContext;
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base.AggregateApiRepositoryBase;
import io.zeitwert.fm.oe.adapter.api.jsonapi.ObjUserApiRepository;
import io.zeitwert.fm.oe.adapter.api.jsonapi.dto.ObjUserDto;
import io.zeitwert.fm.oe.model.ObjUserFM;
import io.zeitwert.fm.oe.model.ObjUserFMRepository;

@Controller("objUserApiRepository")
public class ObjUserApiRepositoryImpl
		extends AggregateApiRepositoryBase<ObjUserFM, ObjUserDto>
		implements ObjUserApiRepository {

	public ObjUserApiRepositoryImpl(
			ObjUserFMRepository repository,
			RequestContext requestCtx,
			ObjUserFMRepository userRepository,
			ObjUserDtoAdapter dtoAdapter) {
		super(ObjUserDto.class, requestCtx, userRepository, repository, dtoAdapter);
	}

}
