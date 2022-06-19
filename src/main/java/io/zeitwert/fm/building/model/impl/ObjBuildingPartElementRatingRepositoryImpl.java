package io.zeitwert.fm.building.model.impl;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.obj.model.base.ObjPartRepositoryBase;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingPartElementRating;
import io.zeitwert.fm.building.model.ObjBuildingPartElementRatingRepository;
import io.zeitwert.fm.building.model.base.ObjBuildingPartElementRatingBase;
import io.zeitwert.fm.building.model.db.Tables;
import io.zeitwert.fm.building.model.db.tables.records.ObjBuildingPartElementRatingRecord;

import java.util.List;

@Component("buildingPartElementRatingRepository")
public class ObjBuildingPartElementRatingRepositoryImpl
		extends ObjPartRepositoryBase<ObjBuilding, ObjBuildingPartElementRating>
		implements ObjBuildingPartElementRatingRepository {

	private static final String PART_TYPE = "obj_building_part_element_rating";

	@Autowired
	//@formatter:off
	protected ObjBuildingPartElementRatingRepositoryImpl(
		final AppContext appContext,
		final DSLContext dslContext
	) {
		super(
			ObjBuilding.class,
			ObjBuildingPartElementRating.class,
			ObjBuildingPartElementRatingBase.class,
			PART_TYPE,
			appContext,
			dslContext
		);
	}
	//@formatter:on

	@Override
	public ObjBuildingPartElementRating doCreate(ObjBuilding obj) {
		ObjBuildingPartElementRatingRecord dbRecord = this.getDSLContext()
				.newRecord(Tables.OBJ_BUILDING_PART_ELEMENT_RATING);
		return this.newPart(obj, dbRecord);
	}

	@Override
	public List<ObjBuildingPartElementRating> doLoad(ObjBuilding obj) {
		//@formatter:off
		io.zeitwert.fm.building.model.db.tables.ObjBuildingPartElementRating table = Tables.OBJ_BUILDING_PART_ELEMENT_RATING;
		Result<ObjBuildingPartElementRatingRecord> dbRecords = this.getDSLContext()
			.selectFrom(table)
			.where(table.OBJ_ID.eq(obj.getId()))
			.orderBy(table.SEQ_NR)
			.fetchInto(table);
		//@formatter:on
		return dbRecords.map(dbRecord -> this.newPart(obj, dbRecord));
	}

}
