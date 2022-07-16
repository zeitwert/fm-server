package io.zeitwert.ddd.obj.model.base;

import io.zeitwert.ddd.aggregate.model.base.AggregateFields;

import java.time.OffsetDateTime;

import org.jooq.Field;
import org.jooq.impl.DSL;

public interface ObjFields extends AggregateFields {

	static final Field<String> OBJ_TYPE_ID = DSL.field("obj_type_id", String.class);

	static final Field<OffsetDateTime> CLOSED_AT = DSL.field("closed_at", OffsetDateTime.class);
	static final Field<Integer> CLOSED_BY_USER_ID = DSL.field("closed_by_user_id", Integer.class);

	static final String TRANSITION_LIST = "obj.transitionList";

}
