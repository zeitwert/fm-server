package io.zeitwert.fm.building.model.impl;

import java.time.LocalDate;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.stereotype.Component;

import io.dddrive.app.service.api.AppContext;
import io.dddrive.jooq.ddd.PartState;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingPartElementRating;
import io.zeitwert.fm.building.model.ObjBuildingPartRating;
import io.zeitwert.fm.building.model.ObjBuildingPartRatingRepository;
import io.zeitwert.fm.building.model.base.ObjBuildingPartRatingBase;
import io.zeitwert.fm.building.model.db.Tables;
import io.zeitwert.fm.building.model.db.tables.records.ObjBuildingPartRatingRecord;
import io.zeitwert.fm.obj.model.base.FMObjPartRepositoryBase;

@Component("buildingPartRatingRepository")
public class ObjBuildingPartRatingRepositoryImpl extends FMObjPartRepositoryBase<ObjBuilding, ObjBuildingPartRating>
		implements ObjBuildingPartRatingRepository {

	private static final String PART_TYPE = "obj_building_part_element_rating";

	protected ObjBuildingPartRatingRepositoryImpl(AppContext appContext, DSLContext dslContext) {
		super(ObjBuilding.class, ObjBuildingPartRating.class, ObjBuildingPartRatingBase.class, PART_TYPE, appContext,
				dslContext);
	}

	@Override
	public void mapProperties() {
		super.mapProperties();
		this.mapField("partCatalog", PartState.BASE, "part_catalog_id", String.class);
		this.mapField("maintenanceStrategy", PartState.BASE, "maintenance_strategy_id", String.class);
		this.mapField("ratingStatus", PartState.BASE, "rating_status_id", String.class);
		this.mapField("ratingDate", PartState.BASE, "rating_date", LocalDate.class);
		this.mapField("ratingUser", PartState.BASE, "rating_user_id", Integer.class);
		this.mapCollection("elementList", "building.elementRatingList", ObjBuildingPartElementRating.class);
	}

	@Override
	public ObjBuildingPartRating doCreate(ObjBuilding obj) {
		ObjBuildingPartRatingRecord dbRecord = this.dslContext().newRecord(Tables.OBJ_BUILDING_PART_RATING);
		return this.getRepositorySPI().newPart(obj, new PartState(dbRecord));
	}

	@Override
	public List<ObjBuildingPartRating> doLoad(ObjBuilding obj) {
		Result<ObjBuildingPartRatingRecord> dbRecords = this.dslContext()
				.selectFrom(Tables.OBJ_BUILDING_PART_RATING)
				.where(Tables.OBJ_BUILDING_PART_RATING.OBJ_ID.eq(obj.getId()))
				.orderBy(Tables.OBJ_BUILDING_PART_RATING.SEQ_NR)
				.fetchInto(Tables.OBJ_BUILDING_PART_RATING);
		return dbRecords.map(dbRecord -> this.getRepositorySPI().newPart(obj, new PartState(dbRecord)));
	}

}
