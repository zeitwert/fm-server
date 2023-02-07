package io.zeitwert.fm.building.model.impl;

import static io.zeitwert.ddd.util.Check.requireThis;

import java.math.BigDecimal;

import org.jooq.DSLContext;
import org.jooq.exception.NoDataFoundException;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingPartRating;
import io.zeitwert.fm.building.model.db.Tables;
import io.zeitwert.fm.building.model.db.tables.records.ObjBuildingRecord;
import io.zeitwert.fm.contact.model.ObjContact;
import io.zeitwert.jooq.persistence.AggregateState;
import io.zeitwert.jooq.persistence.ObjExtnPersistenceProviderBase;

@Configuration("buildingPersistenceProvider")
@DependsOn("codePartListTypeEnum")
public class ObjBuildingPersistenceProvider extends ObjExtnPersistenceProviderBase<ObjBuilding> {

	public ObjBuildingPersistenceProvider(DSLContext dslContext) {
		super(ObjBuilding.class, dslContext);
		this.mapField("name", AggregateState.EXTN, "name", String.class);
		this.mapField("description", AggregateState.EXTN, "description", String.class);
		this.mapField("buildingNr", AggregateState.EXTN, "building_nr", String.class);
		this.mapField("insuranceNr", AggregateState.EXTN, "insurance_nr", String.class);
		this.mapField("plotNr", AggregateState.EXTN, "plot_nr", String.class);
		this.mapField("nationalBuilding", AggregateState.EXTN, "national_building_id", String.class);
		this.mapField("historicPreservation", AggregateState.EXTN, "historic_preservation_id", String.class);
		this.mapField("buildingType", AggregateState.EXTN, "building_type_id", String.class);
		this.mapField("buildingSubType", AggregateState.EXTN, "building_sub_type_id", String.class);
		this.mapField("buildingYear", AggregateState.EXTN, "building_year", Integer.class);
		this.mapField("street", AggregateState.EXTN, "street", String.class);
		this.mapField("zip", AggregateState.EXTN, "zip", String.class);
		this.mapField("city", AggregateState.EXTN, "city", String.class);
		this.mapField("country", AggregateState.EXTN, "country_id", String.class);
		this.mapField("currency", AggregateState.EXTN, "currency_id", String.class);
		this.mapField("geoAddress", AggregateState.EXTN, "geo_address", String.class);
		this.mapField("geoCoordinates", AggregateState.EXTN, "geo_coordinates", String.class);
		this.mapField("geoZoom", AggregateState.EXTN, "geo_zoom", Integer.class);
		this.mapField("coverFoto", AggregateState.EXTN, "cover_foto_id", Integer.class);
		this.mapField("volume", AggregateState.EXTN, "volume", BigDecimal.class);
		this.mapField("areaGross", AggregateState.EXTN, "area_gross", BigDecimal.class);
		this.mapField("areaNet", AggregateState.EXTN, "area_net", BigDecimal.class);
		this.mapField("nrOfFloorsAboveGround", AggregateState.EXTN, "nr_of_floors_above_ground", Integer.class);
		this.mapField("nrOfFloorsBelowGround", AggregateState.EXTN, "nr_of_floors_below_ground", Integer.class);
		this.mapField("insuredValue", AggregateState.EXTN, "insured_value", BigDecimal.class);
		this.mapField("insuredValueYear", AggregateState.EXTN, "insured_value_year", Integer.class);
		this.mapField("notInsuredValue", AggregateState.EXTN, "not_insured_value", BigDecimal.class);
		this.mapField("notInsuredValueYear", AggregateState.EXTN, "not_insured_value_year", Integer.class);
		this.mapField("thirdPartyValue", AggregateState.EXTN, "third_party_value", BigDecimal.class);
		this.mapField("thirdPartyValueYear", AggregateState.EXTN, "third_party_value_year", Integer.class);
		this.mapCollection("ratingList", "building.ratingList", ObjBuildingPartRating.class);
		this.mapCollection("contactSet", "building.contactSet", ObjContact.class);
		this.mapCollection("materialDescriptionSet", "building.materialDescriptionSet", String.class);
		this.mapCollection("conditionDescriptionSet", "building.conditionDescriptionSet", String.class);
		this.mapCollection("measureDescriptionSet", "building.measureDescriptionSet", String.class);
	}

	@Override
	public ObjBuilding doCreate() {
		return this.doCreate(this.dslContext().newRecord(Tables.OBJ_BUILDING));
	}

	@Override
	public ObjBuilding doLoad(Integer objId) {
		requireThis(objId != null, "objId not null");
		ObjBuildingRecord buildingRecord = this.dslContext().fetchOne(Tables.OBJ_BUILDING,
				Tables.OBJ_BUILDING.OBJ_ID.eq(objId));
		if (buildingRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + objId + "]");
		}
		return this.doLoad(objId, buildingRecord);
	}

}
