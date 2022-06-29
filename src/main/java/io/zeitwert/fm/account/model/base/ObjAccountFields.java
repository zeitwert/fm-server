package io.zeitwert.fm.account.model.base;

import io.zeitwert.fm.obj.model.base.FMObjFields;

import org.jooq.Field;
import org.jooq.impl.DSL;

public interface ObjAccountFields extends FMObjFields {

	static final Field<Integer> OBJ_ID = DSL.field("obj_id", Integer.class);

	static final Field<String> KEY = DSL.field("intl_key", String.class);
	static final Field<String> NAME = DSL.field("name", String.class);
	static final Field<String> DESCRIPTION = DSL.field("description", String.class);
	static final Field<String> ACCOUNT_TYPE_ID = DSL.field("account_type_id", String.class);
	static final Field<String> CLIENT_SEGMENT_ID = DSL.field("client_segment_id", String.class);
	static final Field<String> REFERENCE_CURRENCY_ID = DSL.field("reference_currency_id", String.class);
	static final Field<Integer> MAIN_CONTACT_ID = DSL.field("main_contact_id", Integer.class);

}
