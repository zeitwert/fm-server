package io.zeitwert.fm.oe.api.jsonapi.impl

import dddrive.ddd.model.RepositoryDirectory
import io.zeitwert.app.model.SessionContext
import io.zeitwert.dddrive.api.jsonapi.base.AggregateDtoRepositoryBase
import io.zeitwert.fm.oe.api.jsonapi.dto.ObjUserDto
import io.zeitwert.fm.oe.model.ObjUser
import io.zeitwert.fm.oe.model.ObjUserRepository
import org.springframework.stereotype.Controller

@Controller("objUserApiRepository")
open class ObjUserDtoRepositoryImpl(
	directory: RepositoryDirectory,
	repository: ObjUserRepository,
	adapter: ObjUserDtoAdapter,
	sessionCtx: SessionContext,
) : AggregateDtoRepositoryBase<ObjUser, ObjUserDto>(
		resourceClass = ObjUserDto::class.java,
		directory = directory,
		repository = repository,
		adapter = adapter,
		sessionCtx = sessionCtx,
	)
