package io.zeitwert.ddd.obj.model.base;

import org.jooq.Field;
import org.jooq.impl.DSL;

public interface ObjPartItemFields extends ObjPartFields {

	static final Field<String> ITEM_ID = DSL.field("item_id", String.class);

}
