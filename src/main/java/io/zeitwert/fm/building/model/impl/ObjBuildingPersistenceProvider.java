package io.zeitwert.fm.building.model.impl;

import static io.zeitwert.ddd.util.Check.requireThis;

import java.math.BigDecimal;

import org.jooq.DSLContext;
import org.jooq.exception.NoDataFoundException;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import io.zeitwert.ddd.persistence.jooq.base.ObjExtnPersistenceProviderBase;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingPartRating;
import io.zeitwert.fm.building.model.ObjBuildingRepository;
import io.zeitwert.fm.building.model.base.ObjBuildingBase;
import io.zeitwert.fm.building.model.db.Tables;
import io.zeitwert.fm.building.model.db.tables.records.ObjBuildingRecord;
import io.zeitwert.fm.contact.model.ObjContact;

@Configuration("buildingPersistenceProvider")
@DependsOn("codePartListTypeEnum")
public class ObjBuildingPersistenceProvider extends ObjExtnPersistenceProviderBase<ObjBuilding> {

	public ObjBuildingPersistenceProvider(DSLContext dslContext) {
		super(ObjBuildingRepository.class, ObjBuildingBase.class, dslContext);
		this.mapField("name", EXTN, "name", String.class);
		this.mapField("description", EXTN, "description", String.class);
		this.mapField("buildingNr", EXTN, "building_nr", String.class);
		this.mapField("insuranceNr", EXTN, "insurance_nr", String.class);
		this.mapField("plotNr", EXTN, "plot_nr", String.class);
		this.mapField("nationalBuilding", EXTN, "national_building_id", String.class);
		this.mapField("historicPreservation", EXTN, "historic_preservation_id", String.class);
		this.mapField("buildingType", EXTN, "building_type_id", String.class);
		this.mapField("buildingSubType", EXTN, "building_sub_type_id", String.class);
		this.mapField("buildingYear", EXTN, "building_year", Integer.class);
		this.mapField("street", EXTN, "street", String.class);
		this.mapField("zip", EXTN, "zip", String.class);
		this.mapField("city", EXTN, "city", String.class);
		this.mapField("country", EXTN, "country_id", String.class);
		this.mapField("currency", EXTN, "currency_id", String.class);
		this.mapField("geoAddress", EXTN, "geo_address", String.class);
		this.mapField("geoCoordinates", EXTN, "geo_coordinates", String.class);
		this.mapField("geoZoom", EXTN, "geo_zoom", Integer.class);
		this.mapField("coverFoto", EXTN, "cover_foto_id", Integer.class);
		this.mapField("volume", EXTN, "volume", BigDecimal.class);
		this.mapField("areaGross", EXTN, "area_gross", BigDecimal.class);
		this.mapField("areaNet", EXTN, "area_net", BigDecimal.class);
		this.mapField("nrOfFloorsAboveGround", EXTN, "nr_of_floors_above_ground", Integer.class);
		this.mapField("nrOfFloorsBelowGround", EXTN, "nr_of_floors_below_ground", Integer.class);
		this.mapField("insuredValue", EXTN, "insured_value", BigDecimal.class);
		this.mapField("insuredValueYear", EXTN, "insured_value_year", Integer.class);
		this.mapField("notInsuredValue", EXTN, "not_insured_value", BigDecimal.class);
		this.mapField("notInsuredValueYear", EXTN, "not_insured_value_year", Integer.class);
		this.mapField("thirdPartyValue", EXTN, "third_party_value", BigDecimal.class);
		this.mapField("thirdPartyValueYear", EXTN, "third_party_value_year", Integer.class);
		this.mapCollection("ratingList", "building.ratingList", ObjBuildingPartRating.class);
		this.mapCollection("contactSet", "building.contactSet", ObjContact.class);
		this.mapCollection("materialDescriptionSet", "building.materialDescriptionSet", String.class);
		this.mapCollection("conditionDescriptionSet", "building.conditionDescriptionSet", String.class);
		this.mapCollection("measureDescriptionSet", "building.measureDescriptionSet", String.class);
	}

	@Override
	public Class<?> getEntityClass() {
		return ObjBuilding.class;
	}

	@Override
	public ObjBuilding doCreate() {
		return this.doCreate(this.getDSLContext().newRecord(Tables.OBJ_BUILDING));
	}

	@Override
	public ObjBuilding doLoad(Integer objId) {
		requireThis(objId != null, "objId not null");
		ObjBuildingRecord buildingRecord = this.getDSLContext().fetchOne(Tables.OBJ_BUILDING,
				Tables.OBJ_BUILDING.OBJ_ID.eq(objId));
		if (buildingRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + objId + "]");
		}
		return this.doLoad(objId, buildingRecord);
	}

}
