package io.zeitwert.ddd.obj.model.base;

import io.zeitwert.ddd.aggregate.model.base.AggregateFields;

import org.jooq.Field;
import org.jooq.impl.DSL;

public interface ObjFields extends AggregateFields {

	static final Field<String> OBJ_TYPE_ID = DSL.field("obj_type_id", String.class);

	static final String TRANSITION_LIST = "obj.transitionList";
	static final String AREA_SET = "obj.areaSet";

}
