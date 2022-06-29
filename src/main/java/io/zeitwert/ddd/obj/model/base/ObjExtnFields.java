
package io.zeitwert.ddd.obj.model.base;

import org.jooq.Field;
import org.jooq.impl.DSL;

public interface ObjExtnFields {

	static final Field<Integer> OBJ_ID = DSL.field("obj_id", Integer.class);
	static final Field<Integer> TENANT_ID = DSL.field("tenant_id", Integer.class);

}
