
package io.zeitwert.fm.building.model.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.obj.model.ObjRepository;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingRepository;
import io.zeitwert.fm.building.model.base.ObjBuildingBase;
import io.zeitwert.fm.building.model.db.Tables;
import io.zeitwert.fm.building.model.db.tables.records.ObjBuildingVRecord;
import io.zeitwert.fm.obj.model.base.FMObjRepositoryBase;

@Component("objBuildingRepository")
public class ObjBuildingRepositoryImpl extends FMObjRepositoryBase<ObjBuilding, ObjBuildingVRecord>
		implements ObjBuildingRepository {

	private static final String AGGREGATE_TYPE = "obj_building";

	protected ObjBuildingRepositoryImpl(AppContext appContext) {
		super(ObjBuildingRepository.class, ObjBuilding.class, ObjBuildingBase.class, AGGREGATE_TYPE, appContext);
	}

	@Override
	@PostConstruct
	public void registerPartRepositories() {
		super.registerPartRepositories();
		this.addPartRepository(ObjRepository.getItemRepository());
		this.addPartRepository(ObjBuildingRepository.getRatingRepository());
		this.addPartRepository(ObjBuildingRepository.getElementRepository());
	}

	@Override
	protected boolean hasAccountId() {
		return true;
	}

	@Override
	public List<ObjBuildingVRecord> doFind(QuerySpec querySpec) {
		return this.doFind(Tables.OBJ_BUILDING_V, Tables.OBJ_BUILDING_V.ID, querySpec);
	}

}
