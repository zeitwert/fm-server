package io.zeitwert.fm.account.model.base;

import io.zeitwert.ddd.obj.model.base.ObjExtnFields;

import org.jooq.Field;
import org.jooq.impl.DSL;

public interface ObjAccountFields extends ObjExtnFields {

	static final Field<Integer> ACCOUNT_ID = DSL.field("account_id", Integer.class);

	static final Field<String> KEY = DSL.field("intl_key", String.class);
	static final Field<String> NAME = DSL.field("name", String.class);
	static final Field<String> DESCRIPTION = DSL.field("description", String.class);
	static final Field<String> ACCOUNT_TYPE_ID = DSL.field("account_type_id", String.class);
	static final Field<String> CLIENT_SEGMENT_ID = DSL.field("client_segment_id", String.class);
	static final Field<String> REFERENCE_CURRENCY_ID = DSL.field("reference_currency_id", String.class);
	static final Field<Integer> LOGO_IMAGE = DSL.field("logo_img_id", Integer.class);
	static final Field<Integer> BANNER_IMAGE = DSL.field("banner_img_id", Integer.class);
	static final Field<Integer> MAIN_CONTACT_ID = DSL.field("main_contact_id", Integer.class);

}
