package io.zeitwert.ddd.doc.model.base;

import org.jooq.Field;
import org.jooq.impl.DSL;

public interface DocPartItemFields extends DocPartFields {

	static final Field<String> ITEM_ID = DSL.field("item_id", String.class);

}
