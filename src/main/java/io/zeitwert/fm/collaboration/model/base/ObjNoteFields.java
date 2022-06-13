package io.zeitwert.fm.collaboration.model.base;

import io.zeitwert.ddd.item.model.base.ItemFields;

import java.time.OffsetDateTime;

import org.jooq.Field;
import org.jooq.impl.DSL;

public interface ObjNoteFields extends ItemFields {

	static final Field<Integer> OBJ_ID = DSL.field("obj_id", Integer.class);

	static final Field<Integer> RELATED_TO_ID = DSL.field("related_to_id", Integer.class);
	static final Field<String> NOTE_TYPE_ID = DSL.field("note_type_id", String.class);
	static final Field<String> SUBJECT = DSL.field("subject", String.class);
	static final Field<String> CONTENT = DSL.field("content", String.class);
	static final Field<Boolean> IS_PRIVATE = DSL.field("is_private", Boolean.class);

	static final Field<OffsetDateTime> CREATED_AT = DSL.field("created_at", OffsetDateTime.class);
	static final Field<Integer> CREATED_BY_USER_ID = DSL.field("created_by_user_id", Integer.class);
	static final Field<OffsetDateTime> MODIFIED_AT = DSL.field("modified_at", OffsetDateTime.class);
	static final Field<Integer> MODIFIED_BY_USER_ID = DSL.field("modified_by_user_id", Integer.class);

}
