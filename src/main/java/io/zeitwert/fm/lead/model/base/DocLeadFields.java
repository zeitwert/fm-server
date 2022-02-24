package io.zeitwert.fm.lead.model.base;

import io.zeitwert.fm.doc.model.base.FMDocFields;

import org.jooq.Field;
import org.jooq.impl.DSL;

public interface DocLeadFields extends FMDocFields {

	static final Field<Integer> DOC_ID = DSL.field("doc_id", Integer.class);

	static final Field<Integer> ACCOUNT_ID = DSL.field("account_id", Integer.class);
	static final Field<String> SUBJECT = DSL.field("subject", String.class);
	static final Field<String> DESCRIPTION = DSL.field("description", String.class);
	static final Field<String> LEAD_SOURCE_ID = DSL.field("lead_source_id", String.class);
	static final Field<String> LEAD_RATING_ID = DSL.field("lead_rating_id", String.class);
	static final Field<Integer> CONTACT_ID = DSL.field("contact_id", Integer.class);
	static final Field<String> SALUTATION_ID = DSL.field("salutation_id", String.class);
	static final Field<String> TITLE_ID = DSL.field("title_id", String.class);
	static final Field<String> FIRST_NAME = DSL.field("first_name", String.class);
	static final Field<String> LAST_NAME = DSL.field("last_name", String.class);
	static final Field<String> PHONE = DSL.field("phone", String.class);
	static final Field<String> MOBILE = DSL.field("mobile", String.class);
	static final Field<String> EMAIL = DSL.field("email", String.class);
	static final Field<String> STREET = DSL.field("street", String.class);
	static final Field<String> ZIP = DSL.field("zip", String.class);
	static final Field<String> CITY = DSL.field("city", String.class);
	static final Field<String> STATE = DSL.field("state", String.class);
	static final Field<String> COUNTRY_ID = DSL.field("country_id", String.class);

}
