package io.zeitwert.fm.oe.api.jsonapi.impl

import dddrive.ddd.model.RepositoryDirectory
import io.zeitwert.app.api.jsonapi.base.AggregateDtoRepositoryBase
import io.zeitwert.app.session.model.SessionContext
import io.zeitwert.fm.oe.api.jsonapi.dto.ObjTenantDto
import io.zeitwert.fm.oe.model.ObjTenant
import io.zeitwert.fm.oe.model.ObjTenantRepository
import org.springframework.stereotype.Controller

@Controller("objTenantApiRepository")
open class ObjTenantDtoRepositoryImpl(
	directory: RepositoryDirectory,
	repository: ObjTenantRepository,
	adapter: ObjTenantDtoAdapter,
	sessionCtx: SessionContext,
) : AggregateDtoRepositoryBase<ObjTenant, ObjTenantDto>(
		resourceClass = ObjTenantDto::class.java,
		directory = directory,
		repository = repository,
		adapter = adapter,
		sessionCtx = sessionCtx,
	)
