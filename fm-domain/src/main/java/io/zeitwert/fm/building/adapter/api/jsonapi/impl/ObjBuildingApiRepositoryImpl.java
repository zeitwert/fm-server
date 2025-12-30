package io.zeitwert.fm.building.adapter.api.jsonapi.impl;

import io.zeitwert.dddrive.app.model.SessionContext;
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base.AggregateApiRepositoryBase;
import io.zeitwert.fm.building.adapter.api.jsonapi.ObjBuildingApiRepository;
import io.zeitwert.fm.building.adapter.api.jsonapi.dto.ObjBuildingDto;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingRepository;
import io.zeitwert.fm.oe.model.ObjUserRepository;
import org.springframework.stereotype.Controller;

@Controller("objBuildingApiRepository")
public class ObjBuildingApiRepositoryImpl
		extends AggregateApiRepositoryBase<ObjBuilding, ObjBuildingDto>
		implements ObjBuildingApiRepository {

	public ObjBuildingApiRepositoryImpl(
			ObjBuildingRepository repository,
			SessionContext requestCtx,
			ObjUserRepository userRepository,
			ObjBuildingDtoAdapter dtoAdapter) {
		super(ObjBuildingDto.class, requestCtx, userRepository, repository, dtoAdapter);
	}

}
