
package io.zeitwert.ddd.oe.adapter.api.jsonapi.impl;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Controller;

import io.zeitwert.ddd.aggregate.adapter.api.jsonapi.base.AggregateApiRepositoryBase;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.ObjTenantApiRepository;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.dto.ObjTenantDto;
import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.ddd.oe.model.ObjTenantRepository;
import io.zeitwert.ddd.oe.model.db.tables.records.ObjTenantVRecord;
import io.zeitwert.ddd.oe.service.api.UserService;
import io.zeitwert.ddd.session.model.RequestContext;

@Controller("objTenantApiRepository")
@DependsOn("objUserApiRepository")
public class ObjTenantApiRepositoryImpl
		extends AggregateApiRepositoryBase<ObjTenant, ObjTenantVRecord, ObjTenantDto>
		implements ObjTenantApiRepository {

	public ObjTenantApiRepositoryImpl(ObjTenantRepository repository, RequestContext requestCtx,
			UserService userService) {
		super(ObjTenantDto.class, requestCtx, userService, repository, ObjTenantDtoAdapter.getInstance());
	}

}
