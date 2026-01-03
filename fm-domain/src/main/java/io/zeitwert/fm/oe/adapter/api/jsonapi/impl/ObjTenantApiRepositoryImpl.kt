package io.zeitwert.fm.oe.adapter.api.jsonapi.impl

import dddrive.ddd.core.model.RepositoryDirectory
import io.zeitwert.dddrive.app.model.SessionContext
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base.GenericAggregateApiRepositoryBase
import io.zeitwert.fm.oe.adapter.api.jsonapi.dto.ObjTenantDto
import io.zeitwert.fm.oe.model.ObjTenant
import io.zeitwert.fm.oe.model.ObjTenantRepository
import org.springframework.stereotype.Controller

@Controller("objTenantApiRepository")
open class ObjTenantApiRepositoryImpl(
	directory: RepositoryDirectory,
	repository: ObjTenantRepository,
	adapter: ObjTenantDtoAdapter,
	sessionCtx: SessionContext,
) : GenericAggregateApiRepositoryBase<ObjTenant, ObjTenantDto>(
	resourceClass = ObjTenantDto::class.java,
	directory = directory,
	repository = repository,
	adapter = adapter,
	sessionCtx = sessionCtx,
)
