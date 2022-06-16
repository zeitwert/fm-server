
package io.zeitwert.fm.building.adapter.api.jsonapi.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.zeitwert.fm.building.adapter.api.jsonapi.ObjBuildingApiRepository;
import io.zeitwert.fm.building.adapter.api.jsonapi.dto.ObjBuildingDto;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingRepository;
import io.zeitwert.fm.building.model.db.tables.records.ObjBuildingVRecord;
import io.zeitwert.ddd.aggregate.adapter.api.jsonapi.base.AggregateApiAdapter;
import io.zeitwert.ddd.session.model.SessionInfo;

@Controller("objBuildingApiRepository")
public class ObjBuildingApiRepositoryImpl extends AggregateApiAdapter<ObjBuilding, ObjBuildingVRecord, ObjBuildingDto>
		implements ObjBuildingApiRepository {

	@Autowired
	public ObjBuildingApiRepositoryImpl(final ObjBuildingRepository repository, SessionInfo sessionInfo) {
		super(ObjBuildingDto.class, sessionInfo, repository, ObjBuildingDtoBridge.getInstance());
	}

}
