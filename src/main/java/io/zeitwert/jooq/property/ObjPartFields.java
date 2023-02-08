package io.zeitwert.jooq.property;

import org.jooq.Field;
import org.jooq.impl.DSL;

public interface ObjPartFields extends PartFields {

	static final Field<Integer> OBJ_ID = DSL.field("obj_id", Integer.class);
	static final Field<Integer> TENANT_ID = DSL.field("tenant_id", Integer.class);

}
