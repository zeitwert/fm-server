package io.zeitwert.fm.contact.model.base;

import io.zeitwert.ddd.obj.model.base.ObjPartFields;

import org.jooq.Field;
import org.jooq.impl.DSL;

public interface ObjContactPartAddressFields extends ObjPartFields {

	static final Field<String> ADDRESS_CHANNEL_ID = DSL.field("address_channel_id", String.class);
	static final Field<String> NAME = DSL.field("name", String.class);
	static final Field<String> STREET = DSL.field("street", String.class);
	static final Field<String> ZIP = DSL.field("zip", String.class);
	static final Field<String> CITY = DSL.field("city", String.class);
	static final Field<String> COUNTRY_ID = DSL.field("country_id", String.class);

}
