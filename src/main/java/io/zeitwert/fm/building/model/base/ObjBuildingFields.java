package io.zeitwert.fm.building.model.base;

import io.zeitwert.fm.obj.model.base.FMObjFields;

import java.math.BigDecimal;

import org.jooq.Field;
import org.jooq.impl.DSL;

public interface ObjBuildingFields extends FMObjFields {

	static final Field<Integer> OBJ_ID = DSL.field("obj_id", Integer.class);

	// database fields
	static final Field<Integer> ACCOUNT_ID = DSL.field("account_id", Integer.class);
	static final Field<String> NAME = DSL.field("name", String.class);
	static final Field<String> DESCRIPTION = DSL.field("description", String.class);
	static final Field<String> BUILDING_NR = DSL.field("building_nr", String.class);
	static final Field<String> BUILDING_INSURANCE_NR = DSL.field("building_insurance_nr", String.class);
	static final Field<String> PLOT_NR = DSL.field("plot_nr", String.class);
	static final Field<String> NATIONAL_BUILDING_ID = DSL.field("national_building_id", String.class);
	static final Field<String> HISTORIC_PRESERVERATION_ID = DSL.field("historic_preservation_id", String.class);
	static final Field<String> BUILDING_TYPE_ID = DSL.field("building_type_id", String.class);
	static final Field<String> BUILDING_SUB_TYPE_ID = DSL.field("building_sub_type_id", String.class);
	static final Field<Integer> BUILDING_YEAR = DSL.field("building_year", Integer.class);
	static final Field<String> STREET = DSL.field("street", String.class);
	static final Field<String> ZIP = DSL.field("zip", String.class);
	static final Field<String> CITY = DSL.field("city", String.class);
	static final Field<String> COUNTRY_ID = DSL.field("country_id", String.class);
	static final Field<String> CURRENCY_ID = DSL.field("currency_id", String.class);
	static final Field<String> GEO_ADDRESS = DSL.field("geo_address", String.class);
	static final Field<String> GEO_COORDINATES = DSL.field("geo_coordinates", String.class);
	static final Field<Integer> GEO_ZOOM = DSL.field("geo_zoom", Integer.class);
	static final Field<Integer> COVER_FOTO_ID = DSL.field("cover_foto_id", Integer.class);
	static final Field<BigDecimal> VOLUME = DSL.field("volume", BigDecimal.class);
	static final Field<BigDecimal> AREA_GROSS = DSL.field("area_gross", BigDecimal.class);
	static final Field<BigDecimal> AREA_NET = DSL.field("area_net", BigDecimal.class);
	static final Field<Integer> NR_OF_FLOORS_ABOVE_GROUND = DSL.field("nr_of_floors_above_ground", Integer.class);
	static final Field<Integer> NR_OF_FLOORS_BELOW_GROUND = DSL.field("nr_of_floors_below_ground", Integer.class);
	static final Field<BigDecimal> INSURED_VALUE = DSL.field("insured_value", BigDecimal.class);
	static final Field<Integer> INSURED_VALUE_YEAR = DSL.field("insured_value_year", Integer.class);
	static final Field<BigDecimal> NOT_INSURED_VALUE = DSL.field("not_insured_value", BigDecimal.class);
	static final Field<Integer> NOT_INSURED_VALUE_YEAR = DSL.field("not_insured_value_year", Integer.class);
	static final Field<BigDecimal> THIRD_PARTY_VALUE = DSL.field("third_party_value", BigDecimal.class);
	static final Field<Integer> THIRD_PARTY_VALUE_YEAR = DSL.field("third_party_value_year", Integer.class);

	// collections
	static final String RATING_LIST = "building.ratingList";
	static final String MATERIAL_DESCRIPTION_SET = "building.materialDescriptionSet";
	static final String CONDITION_DESCRIPTION_SET = "building.conditionDescriptionSet";
	static final String MEASURE_DESCRIPTION_SET = "building.measureDescriptionSet";

}
