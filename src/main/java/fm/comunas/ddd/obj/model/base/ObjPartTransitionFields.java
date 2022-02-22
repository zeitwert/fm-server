package fm.comunas.ddd.obj.model.base;

import java.time.OffsetDateTime;

import org.jooq.Field;
import org.jooq.JSON;
import org.jooq.impl.DSL;

public interface ObjPartTransitionFields extends ObjPartFields {

	static final Field<Integer> USER_ID = DSL.field("user_id", Integer.class);
	static final Field<OffsetDateTime> MODIFIED_AT = DSL.field("modified_at", OffsetDateTime.class);
	static final Field<JSON> CHANGES = DSL.field("changes", JSON.class);

}
