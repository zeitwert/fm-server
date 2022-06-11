package io.zeitwert.fm.building.model.impl;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.obj.model.base.ObjPartRepositoryBase;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingPartElement;
import io.zeitwert.fm.building.model.ObjBuildingPartElementRepository;
import io.zeitwert.fm.building.model.base.ObjBuildingPartElementBase;
import io.zeitwert.fm.building.model.db.Tables;
import io.zeitwert.fm.building.model.db.tables.records.ObjBuildingPartElementRecord;

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
	public ObjBuildingPartElement doCreate(ObjBuilding obj) {
		ObjBuildingPartElementRecord dbRecord = this.getDSLContext().newRecord(Tables.OBJ_BUILDING_PART_ELEMENT);
		return this.newPart(obj, dbRecord);
	}

	@Override
	public List<ObjBuildingPartElement> doLoad(ObjBuilding obj) {
		//@formatter:off
		Result<ObjBuildingPartElementRecord> dbRecords = this.getDSLContext()
			.selectFrom(Tables.OBJ_BUILDING_PART_ELEMENT)
			.where(Tables.OBJ_BUILDING_PART_ELEMENT.OBJ_ID.eq(obj.getId()))
			.orderBy(Tables.OBJ_BUILDING_PART_ELEMENT.SEQ_NR)
			.fetchInto(Tables.OBJ_BUILDING_PART_ELEMENT);
		//@formatter:on
		return dbRecords.map(dbRecord -> this.newPart(obj, dbRecord));
	}

}
