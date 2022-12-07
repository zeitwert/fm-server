
package io.zeitwert.ddd.oe.model.base;

import io.zeitwert.ddd.obj.model.base.ObjExtnFields;

import org.jooq.Field;
import org.jooq.impl.DSL;

public interface ObjUserFields extends ObjExtnFields {

	static final Field<String> EMAIL = DSL.field("email", String.class);
	static final Field<String> NAME = DSL.field("name", String.class);
	static final Field<String> DESCRIPTION = DSL.field("description", String.class);

	static final Field<String> ROLE_LIST = DSL.field("role_list", String.class);
	static final Field<Integer> AVATAR_IMAGE = DSL.field("avatar_img_id", Integer.class);

	static final String TENANT_LIST = "user.tenantList";

	static final Field<String> PASSWORD = DSL.field("password", String.class);
	static final Field<Boolean> NEED_PASSWORD_CHANGE = DSL.field("need_password_change", Boolean.class);

}
