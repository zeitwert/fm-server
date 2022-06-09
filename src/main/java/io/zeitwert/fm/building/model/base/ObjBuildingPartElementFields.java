package io.zeitwert.fm.building.model.base;

import io.zeitwert.ddd.obj.model.base.ObjPartFields;

import org.jooq.Field;
import org.jooq.impl.DSL;

public interface ObjBuildingPartElementFields extends ObjPartFields {

	static final Field<String> BUILDING_PART_ID = DSL.field("building_part_id", String.class);
	static final Field<Integer> VALUE_PART = DSL.field("value_part", Integer.class);
	static final Field<Integer> CONDITION = DSL.field("condition", Integer.class);
	static final Field<Integer> CONDITION_YEAR = DSL.field("condition_year", Integer.class);
	static final Field<Integer> STRAIN = DSL.field("strain", Integer.class);
	static final Field<Integer> STRENGTH = DSL.field("strength", Integer.class);
	static final Field<String> DESCRIPTION = DSL.field("description", String.class);
	static final Field<String> CONDITION_DESCRIPTION = DSL.field("condition_description", String.class);
	static final Field<String> MEASURE_DESCRIPTION = DSL.field("measure_description", String.class);

}
