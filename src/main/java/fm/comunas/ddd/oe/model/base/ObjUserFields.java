package fm.comunas.ddd.oe.model.base;

import fm.comunas.ddd.obj.model.base.ObjFields;

import org.jooq.Field;
import org.jooq.impl.DSL;

public interface ObjUserFields extends ObjFields {

	static final Field<Integer> OBJ_ID = DSL.field("obj_id", Integer.class);

	static final Field<String> EMAIL = DSL.field("email", String.class);
	static final Field<String> NAME = DSL.field("name", String.class);
	static final Field<String> DESCRIPTION = DSL.field("description", String.class);

	static final Field<String> PASSWORD = DSL.field("password", String.class);
	static final Field<String> ROLE_LIST = DSL.field("role_list", String.class);
	static final Field<String> PICTURE = DSL.field("picture", String.class);

}
