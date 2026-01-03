package io.zeitwert.fm.oe.adapter.api.jsonapi.impl

import dddrive.ddd.core.model.RepositoryDirectory
import io.zeitwert.dddrive.app.model.SessionContext
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base.GenericAggregateApiRepositoryBase
import io.zeitwert.fm.oe.adapter.api.jsonapi.dto.ObjUserDto
import io.zeitwert.fm.oe.model.ObjUser
import io.zeitwert.fm.oe.model.ObjUserRepository
import org.springframework.stereotype.Controller

@Controller("objUserApiRepository")
open class ObjUserApiRepositoryImpl(
	directory: RepositoryDirectory,
	repository: ObjUserRepository,
	adapter: ObjUserDtoAdapter,
	sessionCtx: SessionContext,
) : GenericAggregateApiRepositoryBase<ObjUser, ObjUserDto>(
	resourceClass = ObjUserDto::class.java,
	directory = directory,
	repository = repository,
	adapter = adapter,
	sessionCtx = sessionCtx,
)
