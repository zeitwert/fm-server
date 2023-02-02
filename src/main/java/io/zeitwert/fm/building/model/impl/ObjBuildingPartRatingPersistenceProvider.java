
package io.zeitwert.fm.building.model.impl;

import java.time.LocalDate;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.context.annotation.Configuration;

import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingPartElementRating;
import io.zeitwert.fm.building.model.ObjBuildingPartRating;
import io.zeitwert.fm.building.model.ObjBuildingPartRatingRepository;
import io.zeitwert.ddd.obj.model.base.ObjPartPersistenceProviderBase;
import io.zeitwert.fm.building.model.base.ObjBuildingPartRatingBase;
import io.zeitwert.fm.building.model.db.Tables;
import io.zeitwert.fm.building.model.db.tables.records.ObjBuildingPartRatingRecord;
import io.zeitwert.ddd.persistence.jooq.PartState;

@Configuration
public class ObjBuildingPartRatingPersistenceProvider
		extends ObjPartPersistenceProviderBase<ObjBuilding, ObjBuildingPartRating> {

	public ObjBuildingPartRatingPersistenceProvider(DSLContext dslContext) {
		super(ObjBuilding.class, ObjBuildingPartRatingRepository.class, ObjBuildingPartRatingBase.class, dslContext);
		this.mapField("partCatalog", BASE, "part_catalog_id", String.class);
		this.mapField("maintenanceStrategy", BASE, "maintenance_strategy_id", String.class);
		this.mapField("ratingStatus", BASE, "rating_status_id", String.class);
		this.mapField("ratingDate", BASE, "rating_date", LocalDate.class);
		this.mapField("ratingUser", BASE, "rating_user_id", Integer.class);
		this.mapCollection("elementList", "building.elementRatingList", ObjBuildingPartElementRating.class);
	}

	@Override
	public boolean isReal() {
		return true;
	}

	@Override
	public Class<?> getEntityClass() {
		return ObjBuildingPartRating.class;
	}

	@Override
	public ObjBuildingPartRating doCreate(ObjBuilding obj) {
		ObjBuildingPartRatingRecord dbRecord = this.getDSLContext().newRecord(Tables.OBJ_BUILDING_PART_RATING);
		return this.newPart(obj, new PartState(dbRecord));
	}

	@Override
	public List<ObjBuildingPartRating> doLoad(ObjBuilding obj) {
		Result<ObjBuildingPartRatingRecord> dbRecords = this.getDSLContext()
				.selectFrom(Tables.OBJ_BUILDING_PART_RATING)
				.where(Tables.OBJ_BUILDING_PART_RATING.OBJ_ID.eq(obj.getId()))
				.orderBy(Tables.OBJ_BUILDING_PART_RATING.SEQ_NR)
				.fetchInto(Tables.OBJ_BUILDING_PART_RATING);
		return dbRecords.map(dbRecord -> this.newPart(obj, new PartState(dbRecord)));
	}

}
