package io.zeitwert.fm.building.adapter.api.jsonapi.impl

import dddrive.ddd.core.model.RepositoryDirectory
import io.zeitwert.dddrive.app.model.SessionContext
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base.GenericAggregateApiRepositoryBase
import io.zeitwert.fm.building.adapter.api.jsonapi.dto.ObjBuildingDto
import io.zeitwert.fm.building.model.ObjBuilding
import io.zeitwert.fm.building.model.ObjBuildingRepository
import org.springframework.stereotype.Controller

@Controller("objBuildingApiRepository")
open class ObjBuildingApiRepositoryImpl(
	directory: RepositoryDirectory,
	repository: ObjBuildingRepository,
	adapter: ObjBuildingDtoAdapter,
	sessionCtx: SessionContext,
) : GenericAggregateApiRepositoryBase<ObjBuilding, ObjBuildingDto>(
		resourceClass = ObjBuildingDto::class.java,
		directory = directory,
		repository = repository,
		adapter = adapter,
		sessionCtx = sessionCtx,
	)
