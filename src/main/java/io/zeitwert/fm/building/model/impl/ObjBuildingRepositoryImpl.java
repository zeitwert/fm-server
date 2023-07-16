
package io.zeitwert.fm.building.model.impl;

import static io.dddrive.util.Invariant.requireThis;

import java.math.BigDecimal;
import java.util.List;

import org.jooq.exception.NoDataFoundException;
import org.springframework.stereotype.Component;

import io.crnk.core.queryspec.QuerySpec;
import io.dddrive.jooq.ddd.AggregateState;
import io.zeitwert.fm.account.service.api.ObjAccountCache;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingPartElementRatingRepository;
import io.zeitwert.fm.building.model.ObjBuildingPartRating;
import io.zeitwert.fm.building.model.ObjBuildingPartRatingRepository;
import io.zeitwert.fm.building.model.ObjBuildingRepository;
import io.zeitwert.fm.building.model.base.ObjBuildingBase;
import io.zeitwert.fm.building.model.db.Tables;
import io.zeitwert.fm.building.model.db.tables.records.ObjBuildingRecord;
import io.zeitwert.fm.building.model.db.tables.records.ObjBuildingVRecord;
import io.zeitwert.fm.contact.model.ObjContact;
import io.zeitwert.fm.contact.model.ObjContactRepository;
import io.zeitwert.fm.dms.model.ObjDocumentRepository;
import io.zeitwert.fm.obj.model.base.FMObjRepositoryBase;

@Component("objBuildingRepository")
public class ObjBuildingRepositoryImpl extends FMObjRepositoryBase<ObjBuilding, ObjBuildingVRecord>
		implements ObjBuildingRepository {

	private static final String AGGREGATE_TYPE = "obj_building";

	private ObjAccountCache accountCache;
	private ObjContactRepository contactRepository;
	private ObjDocumentRepository documentRepository;
	private ObjBuildingPartRatingRepository ratingRepository;
	private ObjBuildingPartElementRatingRepository elementRepository;

	protected ObjBuildingRepositoryImpl(
			ObjAccountCache accountCache,
			ObjContactRepository contactRepository,
			ObjDocumentRepository documentRepository,
			ObjBuildingPartRatingRepository ratingRepository,
			ObjBuildingPartElementRatingRepository elementRepository) {
		super(ObjBuildingRepository.class, ObjBuilding.class, ObjBuildingBase.class, AGGREGATE_TYPE);
		this.accountCache = accountCache;
		this.contactRepository = contactRepository;
		this.documentRepository = documentRepository;
		this.ratingRepository = ratingRepository;
		this.elementRepository = elementRepository;
	}

	@Override
	public ObjAccountCache getAccountCache() {
		return this.accountCache;
	}

	@Override
	public ObjContactRepository getContactRepository() {
		return this.contactRepository;
	}

	@Override
	public ObjDocumentRepository getDocumentRepository() {
		return this.documentRepository;
	}

	@Override
	public ObjBuildingPartRatingRepository getRatingRepository() {
		return this.ratingRepository;
	}

	@Override
	public ObjBuildingPartElementRatingRepository getElementRepository() {
		return this.elementRepository;
	}

	@Override
	public void mapProperties() {
		super.mapProperties();
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
	public void registerPartRepositories() {
		super.registerPartRepositories();
		this.addPartRepository(this.getRatingRepository());
		this.addPartRepository(this.getElementRepository());
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

	@Override
	public List<ObjBuildingVRecord> doFind(QuerySpec querySpec) {
		return this.doFind(Tables.OBJ_BUILDING_V, Tables.OBJ_BUILDING_V.ID, querySpec);
	}

}
