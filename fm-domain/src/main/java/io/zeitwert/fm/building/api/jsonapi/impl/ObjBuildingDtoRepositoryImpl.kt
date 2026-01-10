package io.zeitwert.fm.building.api.jsonapi.impl

import dddrive.ddd.model.RepositoryDirectory
import io.zeitwert.app.api.jsonapi.base.AggregateDtoRepositoryBase
import io.zeitwert.app.session.model.SessionContext
import io.zeitwert.fm.building.api.jsonapi.dto.ObjBuildingDto
import io.zeitwert.fm.building.model.ObjBuilding
import io.zeitwert.fm.building.model.ObjBuildingRepository
import org.springframework.stereotype.Controller

@Controller("objBuildingApiRepository")
open class ObjBuildingDtoRepositoryImpl(
	directory: RepositoryDirectory,
	repository: ObjBuildingRepository,
	adapter: ObjBuildingDtoAdapter,
	sessionCtx: SessionContext,
) : AggregateDtoRepositoryBase<ObjBuilding, ObjBuildingDto>(
		resourceClass = ObjBuildingDto::class.java,
		directory = directory,
		repository = repository,
		adapter = adapter,
		sessionCtx = sessionCtx,
	)
