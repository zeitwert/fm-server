
package io.zeitwert.fm.oe.adapter.api.jsonapi.impl;

import org.springframework.stereotype.Controller;

import io.dddrive.app.model.RequestContext;
import io.dddrive.ddd.adapter.api.jsonapi.base.AggregateApiRepositoryBase;
import io.dddrive.oe.service.api.ObjUserCache;
import io.zeitwert.fm.oe.adapter.api.jsonapi.ObjTenantApiRepository;
import io.zeitwert.fm.oe.adapter.api.jsonapi.dto.ObjTenantDto;
import io.zeitwert.fm.oe.model.ObjTenantFM;
import io.zeitwert.fm.oe.model.ObjTenantFMRepository;
import io.zeitwert.fm.oe.model.db.tables.records.ObjTenantVRecord;

@Controller("objTenantApiRepository")
public class ObjTenantApiRepositoryImpl
		extends AggregateApiRepositoryBase<ObjTenantFM, ObjTenantVRecord, ObjTenantDto>
		implements ObjTenantApiRepository {

	public ObjTenantApiRepositoryImpl(
			ObjTenantFMRepository repository,
			RequestContext requestCtx,
			ObjUserCache userCache,
			ObjTenantDtoAdapter dtoAdapter) {
		super(ObjTenantDto.class, requestCtx, userCache, repository, dtoAdapter);
	}

}
