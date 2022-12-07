
package io.zeitwert.ddd.oe.adapter.api.jsonapi.impl;

import org.springframework.stereotype.Controller;

import io.zeitwert.ddd.aggregate.adapter.api.jsonapi.base.AggregateApiRepositoryBase;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.ObjTenantApiRepository;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.dto.ObjTenantDto;
import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.ddd.oe.model.ObjTenantRepository;
import io.zeitwert.ddd.oe.model.db.tables.records.ObjTenantVRecord;
import io.zeitwert.ddd.oe.service.api.ObjUserCache;
import io.zeitwert.ddd.session.model.RequestContext;

@Controller("objTenantApiRepository")
public class ObjTenantApiRepositoryImpl
		extends AggregateApiRepositoryBase<ObjTenant, ObjTenantVRecord, ObjTenantDto>
		implements ObjTenantApiRepository {

	public ObjTenantApiRepositoryImpl(ObjTenantRepository repository, RequestContext requestCtx,
			ObjUserCache userCache) {
		super(ObjTenantDto.class, requestCtx, userCache, repository, ObjTenantDtoAdapter.getInstance());
	}

}
