package io.zeitwert.fm.building.model.impl;

import static io.zeitwert.ddd.util.Check.requireThis;

import java.math.BigDecimal;

import org.jooq.DSLContext;
import org.jooq.exception.NoDataFoundException;
import org.springframework.context.annotation.Configuration;

import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingPartRating;
import io.zeitwert.fm.building.model.ObjBuildingRepository;
import io.zeitwert.fm.building.model.base.ObjBuildingBase;
import io.zeitwert.fm.building.model.db.Tables;
import io.zeitwert.fm.building.model.db.tables.records.ObjBuildingRecord;
import io.zeitwert.fm.contact.model.ObjContact;
import io.zeitwert.fm.obj.model.base.FMObjPersistenceProviderBase;

@Configuration("buildingPersistenceProvider")
public class ObjBuildingPersistenceProvider extends FMObjPersistenceProviderBase<ObjBuilding> {

	public ObjBuildingPersistenceProvider(DSLContext dslContext) {
		super(ObjBuildingRepository.class, ObjBuildingBase.class, dslContext);
		this.mapField("extnAccount", DbTableType.EXTN, "account_id", Integer.class);
		this.mapField("name", DbTableType.EXTN, "name", String.class);
		this.mapField("description", DbTableType.EXTN, "description", String.class);
		this.mapField("buildingNr", DbTableType.EXTN, "building_nr", String.class);
		this.mapField("insuranceNr", DbTableType.EXTN, "insurance_nr", String.class);
		this.mapField("plotNr", DbTableType.EXTN, "plot_nr", String.class);
		this.mapField("nationalBuilding", DbTableType.EXTN, "national_building_id", String.class);
		this.mapField("historicPreservation", DbTableType.EXTN, "historic_preservation_id", String.class);
		this.mapField("buildingType", DbTableType.EXTN, "building_type_id", String.class);
		this.mapField("buildingSubType", DbTableType.EXTN, "building_sub_type_id", String.class);
		this.mapField("buildingYear", DbTableType.EXTN, "building_year", Integer.class);
		this.mapField("street", DbTableType.EXTN, "street", String.class);
		this.mapField("zip", DbTableType.EXTN, "zip", String.class);
		this.mapField("city", DbTableType.EXTN, "city", String.class);
		this.mapField("country", DbTableType.EXTN, "country_id", String.class);
		this.mapField("currency", DbTableType.EXTN, "currency_id", String.class);
		this.mapField("geoAddress", DbTableType.EXTN, "geo_address", String.class);
		this.mapField("geoCoordinates", DbTableType.EXTN, "geo_coordinates", String.class);
		this.mapField("geoZoom", DbTableType.EXTN, "geo_zoom", Integer.class);
		this.mapField("coverFoto", DbTableType.EXTN, "cover_foto_id", Integer.class);
		this.mapField("volume", DbTableType.EXTN, "volume", BigDecimal.class);
		this.mapField("areaGross", DbTableType.EXTN, "area_gross", BigDecimal.class);
		this.mapField("areaNet", DbTableType.EXTN, "area_net", BigDecimal.class);
		this.mapField("nrOfFloorsAboveGround", DbTableType.EXTN, "nr_of_floors_above_ground", Integer.class);
		this.mapField("nrOfFloorsBelowGround", DbTableType.EXTN, "nr_of_floors_below_ground", Integer.class);
		this.mapField("insuredValue", DbTableType.EXTN, "insured_value", BigDecimal.class);
		this.mapField("insuredValueYear", DbTableType.EXTN, "insured_value_year", Integer.class);
		this.mapField("notInsuredValue", DbTableType.EXTN, "not_insured_value", BigDecimal.class);
		this.mapField("notInsuredValueYear", DbTableType.EXTN, "not_insured_value_year", Integer.class);
		this.mapField("thirdPartyValue", DbTableType.EXTN, "third_party_value", BigDecimal.class);
		this.mapField("thirdPartyValueYear", DbTableType.EXTN, "third_party_value_year", Integer.class);
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
	public boolean isReal() {
		return true;
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
