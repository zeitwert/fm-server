package io.zeitwert.ddd.aggregate.model.base;

import java.time.OffsetDateTime;
import org.jooq.Field;
import org.jooq.impl.DSL;

public interface AggregateFields {

	static final Field<Integer> ID = DSL.field("id", Integer.class);
	static final Field<Integer> TENANT_ID = DSL.field("tenant_id", Integer.class);
	static final Field<Integer> ACCOUNT_ID = DSL.field("account_id", Integer.class);
	static final Field<Integer> OWNER_ID = DSL.field("owner_id", Integer.class);
	static final Field<String> CAPTION = DSL.field("caption", String.class);

	static final Field<OffsetDateTime> CREATED_AT = DSL.field("created_at", OffsetDateTime.class);
	static final Field<Integer> CREATED_BY_USER_ID = DSL.field("created_by_user_id", Integer.class);
	static final Field<OffsetDateTime> MODIFIED_AT = DSL.field("modified_at", OffsetDateTime.class);
	static final Field<Integer> MODIFIED_BY_USER_ID = DSL.field("modified_by_user_id", Integer.class);

}
