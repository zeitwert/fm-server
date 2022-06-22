package io.zeitwert.fm.building.model.base;

import java.time.LocalDate;

import org.jooq.Field;
import org.jooq.impl.DSL;

import io.zeitwert.ddd.obj.model.base.ObjPartFields;

public interface ObjBuildingPartRatingFields extends ObjPartFields {

	static final Field<String> PART_CATALOG_ID = DSL.field("part_catalog_id", String.class);
	static final Field<String> MAINTENANCE_STRATEGY_ID = DSL.field("maintenance_strategy_id",
			String.class);

	static final Field<String> RATING_STATUS_ID = DSL.field("rating_status_id", String.class);
	static final Field<LocalDate> RATING_DATE = DSL.field("rating_date", LocalDate.class);
	static final Field<Integer> RATING_USER_ID = DSL.field("rating_user_id", Integer.class);

	static final String ELEMENT_RATING_LIST = "building.elementRatingList";

}
