package fm.comunas.ddd.doc.model.base;

import java.time.OffsetDateTime;

import org.jooq.Field;
import org.jooq.JSON;
import org.jooq.impl.DSL;

public interface DocPartTransitionFields {

	static final Field<Integer> USER_ID = DSL.field("user_id", Integer.class);
	static final Field<OffsetDateTime> MODIFIED_AT = DSL.field("modified_at", OffsetDateTime.class);
	static final Field<String> OLD_CASE_STAGE_ID = DSL.field("old_case_stage_id", String.class);
	static final Field<String> NEW_CASE_STAGE_ID = DSL.field("new_case_stage_id", String.class);
	static final Field<JSON> CHANGES = DSL.field("changes", JSON.class);

}
