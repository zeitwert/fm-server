
package io.zeitwert.ddd.collaboration.model.base;

import io.zeitwert.ddd.obj.model.base.ObjExtnFields;

import org.jooq.Field;
import org.jooq.impl.DSL;

public interface ObjNoteFields extends ObjExtnFields {

	static final Field<Integer> RELATED_TO_ID = DSL.field("related_to_id", Integer.class);
	static final Field<String> NOTE_TYPE_ID = DSL.field("note_type_id", String.class);
	static final Field<String> SUBJECT = DSL.field("subject", String.class);
	static final Field<String> CONTENT = DSL.field("content", String.class);
	static final Field<Boolean> IS_PRIVATE = DSL.field("is_private", Boolean.class);

}
