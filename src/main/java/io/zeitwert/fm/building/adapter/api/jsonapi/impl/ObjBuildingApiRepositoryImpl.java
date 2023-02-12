
package io.zeitwert.fm.building.adapter.api.jsonapi.impl;

import org.springframework.stereotype.Controller;

import io.dddrive.app.model.RequestContext;
import io.dddrive.ddd.adapter.api.jsonapi.base.AggregateApiRepositoryBase;
import io.dddrive.oe.service.api.ObjUserCache;
import io.zeitwert.fm.building.adapter.api.jsonapi.ObjBuildingApiRepository;
import io.zeitwert.fm.building.adapter.api.jsonapi.dto.ObjBuildingDto;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingRepository;
import io.zeitwert.fm.building.model.db.tables.records.ObjBuildingVRecord;

@Controller("objBuildingApiRepository")
public class ObjBuildingApiRepositoryImpl
		extends AggregateApiRepositoryBase<ObjBuilding, ObjBuildingVRecord, ObjBuildingDto>
		implements ObjBuildingApiRepository {

	public ObjBuildingApiRepositoryImpl(
			ObjBuildingRepository repository,
			RequestContext requestCtx,
			ObjUserCache userCache,
			ObjBuildingDtoAdapter dtoAdapter) {
		super(ObjBuildingDto.class, requestCtx, userCache, repository, dtoAdapter);
	}

}
