package io.dddrive.jooq.doc;

import org.jooq.Field;
import org.jooq.impl.DSL;

import io.dddrive.jooq.ddd.PartFields;

public interface DocPartFields extends PartFields {

	static final Field<Integer> DOC_ID = DSL.field("doc_id", Integer.class);
	static final Field<Integer> TENANT_ID = DSL.field("tenant_id", Integer.class);

}
