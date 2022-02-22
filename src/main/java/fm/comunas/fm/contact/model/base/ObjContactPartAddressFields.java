package fm.comunas.fm.contact.model.base;

import fm.comunas.ddd.obj.model.base.ObjPartFields;

import org.jooq.Field;
import org.jooq.impl.DSL;

public interface ObjContactPartAddressFields extends ObjPartFields {

	static final Field<String> ADDRESS_TYPE_ID = DSL.field("address_type_id", String.class);
	static final Field<String> NAME = DSL.field("name", String.class);
	static final Field<String> STREET = DSL.field("street", String.class);
	static final Field<String> ZIP = DSL.field("zip", String.class);
	static final Field<String> CITY = DSL.field("city", String.class);
	static final Field<String> STATE = DSL.field("state", String.class);
	static final Field<String> COUNTRY_ID = DSL.field("country_id", String.class);
	static final Field<String> CHANNEL_ID = DSL.field("channel_id", String.class);
	static final Field<Boolean> IS_FAVORITE = DSL.field("is_favorite", Boolean.class);
	static final Field<Boolean> IS_MAIL_ADDRESS = DSL.field("is_mail_address", Boolean.class);

}
