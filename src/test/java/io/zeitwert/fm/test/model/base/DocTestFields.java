
package io.zeitwert.fm.test.model.base;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.jooq.Field;
import org.jooq.JSON;
import org.jooq.impl.DSL;

import io.zeitwert.ddd.doc.model.base.DocExtnFields;

public interface DocTestFields extends DocExtnFields {

	static final Field<String> SHORT_TEXT = DSL.field("short_text", String.class);
	static final Field<String> LONG_TEXT = DSL.field("long_text", String.class);
	static final Field<LocalDate> DATE = DSL.field("date", LocalDate.class);
	static final Field<Integer> INT = DSL.field("int", Integer.class);
	static final Field<Boolean> IS_DONE = DSL.field("is_done", Boolean.class);
	static final Field<JSON> JSON = DSL.field("json", JSON.class);
	static final Field<BigDecimal> NR = DSL.field("nr", BigDecimal.class);

	static final Field<String> COUNTRY_ID = DSL.field("country_id", String.class);
	static final Field<Integer> REF_OBJ_ID = DSL.field("ref_obj_id", Integer.class);
	static final Field<Integer> REF_DOC_ID = DSL.field("ref_doc_id", Integer.class);

	static final String COUNTRY_SET = "test.countrySet";
	static final String NODE_LIST = "test.nodeList";

}
