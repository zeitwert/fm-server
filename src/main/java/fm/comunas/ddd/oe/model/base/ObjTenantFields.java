package fm.comunas.ddd.oe.model.base;

import fm.comunas.ddd.obj.model.base.ObjFields;

import org.jooq.Field;
import org.jooq.impl.DSL;

public interface ObjTenantFields extends ObjFields {

	static final Field<Integer> OBJ_ID = DSL.field("obj_id", Integer.class);

	static final Field<String> EXTL_KEY = DSL.field("extl_key", String.class);
	static final Field<String> NAME = DSL.field("name", String.class);
	static final Field<String> DESCRIPTION = DSL.field("description", String.class);

}
