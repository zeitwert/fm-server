
package io.zeitwert.ddd.oe.adapter.api.jsonapi.impl;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Controller;

import io.zeitwert.ddd.aggregate.adapter.api.jsonapi.base.AggregateApiRepositoryBase;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.ObjTenantApiRepository;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.dto.ObjTenantDto;
import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.ddd.oe.model.ObjTenantRepository;
import io.zeitwert.ddd.oe.model.db.tables.records.ObjTenantVRecord;
import io.zeitwert.ddd.session.model.RequestContext;

@Controller("objTenantApiRepository")
@DependsOn("objUserApiRepository")
public class ObjTenantApiRepositoryImpl
		extends AggregateApiRepositoryBase<ObjTenant, ObjTenantVRecord, ObjTenantDto>
		implements ObjTenantApiRepository {

	public ObjTenantApiRepositoryImpl(final ObjTenantRepository repository, RequestContext requestCtx) {
		super(ObjTenantDto.class, requestCtx, repository, ObjTenantDtoAdapter.getInstance());
	}

}
