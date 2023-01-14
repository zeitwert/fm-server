package io.zeitwert.fm.task.model.base;

import java.time.OffsetDateTime;

import org.jooq.Field;
import org.jooq.impl.DSL;

import io.zeitwert.ddd.doc.model.base.DocExtnFields;

public interface DocTaskFields extends DocExtnFields {

	static final Field<Integer> ACCOUNT_ID = DSL.field("account_id", Integer.class);

	static final Field<Integer> RELATED_OBJ_ID = DSL.field("related_obj_id", Integer.class);
	static final Field<Integer> RELATED_DOC_ID = DSL.field("related_doc_id", Integer.class);

	static final Field<String> SUBJECT = DSL.field("subject", String.class);
	static final Field<String> CONTENT = DSL.field("content", String.class);
	static final Field<Boolean> IS_PRIVATE = DSL.field("is_private", Boolean.class);

	static final Field<String> PRIORITY_ID = DSL.field("priority_id", String.class);
	static final Field<OffsetDateTime> DUE_AT = DSL.field("due_at", OffsetDateTime.class);
	static final Field<OffsetDateTime> REMIND_AT = DSL.field("remind_at", OffsetDateTime.class);

}
