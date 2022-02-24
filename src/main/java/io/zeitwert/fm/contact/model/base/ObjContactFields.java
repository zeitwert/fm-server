package io.zeitwert.fm.contact.model.base;

import io.zeitwert.fm.obj.model.base.FMObjFields;

import java.time.LocalDate;

import org.jooq.Field;
import org.jooq.impl.DSL;

public interface ObjContactFields extends FMObjFields {

	static final Field<Integer> OBJ_ID = DSL.field("obj_id", Integer.class);

	static final Field<Integer> ACCOUNT_ID = DSL.field("account_id", Integer.class);
	static final Field<String> CONTACT_ROLE_ID = DSL.field("contact_role_id", String.class);
	static final Field<String> SALUTATION_ID = DSL.field("salutation_id", String.class);
	static final Field<String> TITLE_ID = DSL.field("title_id", String.class);
	static final Field<String> FIRST_NAME = DSL.field("first_name", String.class);
	static final Field<String> LAST_NAME = DSL.field("last_name", String.class);
	static final Field<LocalDate> BIRTH_DATE = DSL.field("birth_date", LocalDate.class);
	static final Field<String> PHONE = DSL.field("phone", String.class);
	static final Field<String> MOBILE = DSL.field("mobile", String.class);
	static final Field<String> EMAIL = DSL.field("email", String.class);
	static final Field<String> DESCRIPTION = DSL.field("description", String.class);

	static final String ADDRESS_LIST = "contact.addressList";
	static final String ANNIVERSARY_LIST = "contact.anniversaryList";

}
