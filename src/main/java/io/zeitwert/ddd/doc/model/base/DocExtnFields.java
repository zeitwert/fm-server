package io.zeitwert.ddd.doc.model.base;

import org.jooq.Field;
import org.jooq.impl.DSL;

public interface DocExtnFields {

	static final Field<Integer> DOC_ID = DSL.field("doc_id", Integer.class);
	static final Field<Integer> TENANT_ID = DSL.field("tenant_id", Integer.class);

}
