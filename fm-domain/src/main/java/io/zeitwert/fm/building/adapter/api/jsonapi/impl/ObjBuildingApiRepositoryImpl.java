
package io.zeitwert.fm.building.adapter.api.jsonapi.impl;

import org.springframework.stereotype.Controller;

import io.zeitwert.dddrive.app.model.RequestContext;
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base.AggregateApiRepositoryBase;
import io.zeitwert.fm.building.adapter.api.jsonapi.ObjBuildingApiRepository;
import io.zeitwert.fm.building.adapter.api.jsonapi.dto.ObjBuildingDto;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingRepository;
import io.zeitwert.fm.building.model.db.tables.records.ObjBuildingVRecord;
import io.zeitwert.fm.oe.model.ObjUserFMRepository;

@Controller("objBuildingApiRepository")
public class ObjBuildingApiRepositoryImpl
		extends AggregateApiRepositoryBase<ObjBuilding, ObjBuildingDto>
		implements ObjBuildingApiRepository {

	public ObjBuildingApiRepositoryImpl(
			ObjBuildingRepository repository,
			RequestContext requestCtx,
			ObjUserFMRepository userCache,
			ObjBuildingDtoAdapter dtoAdapter) {
		super(ObjBuildingDto.class, requestCtx, userCache, repository, dtoAdapter);
	}

}
