
package io.zeitwert.fm.building.model.impl;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.context.annotation.Configuration;

import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingPartElementRating;
import io.zeitwert.fm.building.model.ObjBuildingPartElementRatingRepository;
import io.zeitwert.fm.building.model.base.ObjBuildingPartElementRatingBase;
import io.zeitwert.fm.building.model.db.Tables;
import io.zeitwert.fm.building.model.db.tables.records.ObjBuildingPartElementRatingRecord;
import io.zeitwert.ddd.persistence.jooq.PartState;
import io.zeitwert.ddd.persistence.jooq.base.ObjPartPersistenceProviderBase;

@Configuration
public class ObjBuildingPartElementRatingPersistenceProvider
		extends ObjPartPersistenceProviderBase<ObjBuilding, ObjBuildingPartElementRating> {

	public ObjBuildingPartElementRatingPersistenceProvider(DSLContext dslContext) {
		super(ObjBuilding.class, ObjBuildingPartElementRatingRepository.class, ObjBuildingPartElementRatingBase.class,
				dslContext);
		this.mapField("buildingPart", PartState.BASE, "building_part_id", String.class);
		this.mapField("weight", PartState.BASE, "weight", Integer.class);
		this.mapField("condition", PartState.BASE, "condition", Integer.class);
		this.mapField("ratingYear", PartState.BASE, "condition_year", Integer.class);
		this.mapField("strain", PartState.BASE, "strain", Integer.class);
		this.mapField("strength", PartState.BASE, "strength", Integer.class);
		this.mapField("description", PartState.BASE, "description", String.class);
		this.mapField("conditionDescription", PartState.BASE, "condition_description", String.class);
		this.mapField("measureDescription", PartState.BASE, "measure_description", String.class);
	}

	@Override
	public Class<?> getEntityClass() {
		return ObjBuildingPartElementRating.class;
	}

	@Override
	public ObjBuildingPartElementRating doCreate(ObjBuilding obj) {
		ObjBuildingPartElementRatingRecord dbRecord = this.dslContext()
				.newRecord(Tables.OBJ_BUILDING_PART_ELEMENT_RATING);
		return this.getRepositorySPI().newPart(obj, new PartState(dbRecord));
	}

	@Override
	public List<ObjBuildingPartElementRating> doLoad(ObjBuilding obj) {
		Result<ObjBuildingPartElementRatingRecord> dbRecords = this.dslContext()
				.selectFrom(Tables.OBJ_BUILDING_PART_ELEMENT_RATING)
				.where(Tables.OBJ_BUILDING_PART_ELEMENT_RATING.OBJ_ID.eq(obj.getId()))
				.orderBy(Tables.OBJ_BUILDING_PART_ELEMENT_RATING.SEQ_NR)
				.fetchInto(Tables.OBJ_BUILDING_PART_ELEMENT_RATING);
		return dbRecords.map(dbRecord -> this.getRepositorySPI().newPart(obj, new PartState(dbRecord)));
	}

}
