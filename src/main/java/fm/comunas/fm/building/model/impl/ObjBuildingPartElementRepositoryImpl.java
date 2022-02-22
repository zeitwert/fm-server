package fm.comunas.fm.building.model.impl;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fm.comunas.ddd.app.service.api.AppContext;
import fm.comunas.ddd.obj.model.base.ObjPartRepositoryBase;
import fm.comunas.fm.building.model.ObjBuilding;
import fm.comunas.fm.building.model.ObjBuildingPartElement;
import fm.comunas.fm.building.model.ObjBuildingPartElementRepository;
import fm.comunas.fm.building.model.base.ObjBuildingPartElementBase;
import fm.comunas.fm.building.model.db.Tables;
import fm.comunas.fm.building.model.db.tables.records.ObjBuildingPartElementRecord;

import java.util.List;

@Component("buildingPartElementRepository")
public class ObjBuildingPartElementRepositoryImpl extends ObjPartRepositoryBase<ObjBuilding, ObjBuildingPartElement>
		implements ObjBuildingPartElementRepository {

	private static final String PART_TYPE = "obj_building_part_element";

	@Autowired
	//@formatter:off
	protected ObjBuildingPartElementRepositoryImpl(
		final AppContext appContext,
		final DSLContext dslContext
	) {
		super(
			ObjBuilding.class,
			ObjBuildingPartElement.class,
			ObjBuildingPartElementBase.class,
			PART_TYPE,
			appContext,
			dslContext
		);
	}
	//@formatter:on

	@Override
	public List<ObjBuildingPartElement> doLoad(ObjBuilding obj) {
		//@formatter:off
		Result<ObjBuildingPartElementRecord> dbRecords = this.dslContext
			.selectFrom(Tables.OBJ_BUILDING_PART_ELEMENT)
			.where(Tables.OBJ_BUILDING_PART_ELEMENT.OBJ_ID.eq(obj.getId()))
			.orderBy(Tables.OBJ_BUILDING_PART_ELEMENT.SEQ_NR)
			.fetchInto(Tables.OBJ_BUILDING_PART_ELEMENT);
		//@formatter:on
		return dbRecords.map(dbRecord -> this.newPart(obj, dbRecord));
	}

	@Override
	public ObjBuildingPartElement doCreate(ObjBuilding obj) {
		ObjBuildingPartElementRecord dbRecord = this.dslContext.newRecord(Tables.OBJ_BUILDING_PART_ELEMENT);
		return this.newPart(obj, dbRecord);
	}

}
