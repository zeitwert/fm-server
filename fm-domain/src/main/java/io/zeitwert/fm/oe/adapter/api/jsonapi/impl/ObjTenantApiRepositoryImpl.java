package io.zeitwert.fm.oe.adapter.api.jsonapi.impl;

import io.zeitwert.dddrive.app.model.SessionContext;
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base.AggregateApiRepositoryBase;
import io.zeitwert.fm.oe.adapter.api.jsonapi.ObjTenantApiRepository;
import io.zeitwert.fm.oe.adapter.api.jsonapi.dto.ObjTenantDto;
import io.zeitwert.fm.oe.model.ObjTenant;
import io.zeitwert.fm.oe.model.ObjTenantRepository;
import io.zeitwert.fm.oe.model.ObjUserRepository;
import org.springframework.stereotype.Controller;

@Controller("objTenantApiRepository")
public class ObjTenantApiRepositoryImpl
		extends AggregateApiRepositoryBase<ObjTenant, ObjTenantDto>
		implements ObjTenantApiRepository {

	public ObjTenantApiRepositoryImpl(
			ObjTenantRepository repository,
			SessionContext requestCtx,
			ObjUserRepository userRepository,
			ObjTenantDtoAdapter dtoAdapter) {
		super(ObjTenantDto.class, requestCtx, userRepository, repository, dtoAdapter);
	}

}
