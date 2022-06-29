package io.zeitwert.ddd.doc.model.base;

import io.zeitwert.ddd.part.model.base.PartFields;

import org.jooq.Field;
import org.jooq.impl.DSL;

public interface DocPartFields extends PartFields {

	static final Field<Integer> DOC_ID = DSL.field("doc_id", Integer.class);
	static final Field<Integer> TENANT_ID = DSL.field("tenant_id", Integer.class);

}
