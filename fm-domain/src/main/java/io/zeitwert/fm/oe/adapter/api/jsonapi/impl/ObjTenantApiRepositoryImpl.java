package io.zeitwert.fm.oe.adapter.api.jsonapi.impl;

import org.springframework.stereotype.Controller;

import io.zeitwert.dddrive.app.model.RequestContext;
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base.AggregateApiRepositoryBase;
import io.zeitwert.fm.oe.adapter.api.jsonapi.ObjTenantApiRepository;
import io.zeitwert.fm.oe.adapter.api.jsonapi.dto.ObjTenantDto;
import io.zeitwert.fm.oe.model.ObjTenantFM;
import io.zeitwert.fm.oe.model.ObjTenantFMRepository;
import io.zeitwert.fm.oe.model.ObjUserFMRepository;

@Controller("objTenantApiRepository")
public class ObjTenantApiRepositoryImpl
		extends AggregateApiRepositoryBase<ObjTenantFM, ObjTenantDto>
		implements ObjTenantApiRepository {

	public ObjTenantApiRepositoryImpl(
			ObjTenantFMRepository repository,
			RequestContext requestCtx,
			ObjUserFMRepository userRepository,
			ObjTenantDtoAdapter dtoAdapter) {
		super(ObjTenantDto.class, requestCtx, userRepository, repository, dtoAdapter);
	}

}
