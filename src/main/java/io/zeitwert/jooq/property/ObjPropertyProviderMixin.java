package io.zeitwert.jooq.property;

import java.time.OffsetDateTime;

import io.zeitwert.ddd.obj.model.ObjPartTransition;
import io.zeitwert.jooq.persistence.AggregateState;

public interface ObjPropertyProviderMixin
		extends AggregatePropertyProviderMixin {

	@Override
	default void mapProperties() {
		this.mapField("id", AggregateState.BASE, "id", Integer.class);
		this.mapField("objTypeId", AggregateState.BASE, "obj_type_id", String.class);
		this.mapField("tenant", AggregateState.BASE, "tenant_id", Integer.class);
		this.mapField("accountId", AggregateState.BASE, "account_id", Integer.class);
		this.mapField("owner", AggregateState.BASE, "owner_id", Integer.class);
		this.mapField("caption", AggregateState.BASE, "caption", String.class);
		this.mapField("version", AggregateState.BASE, "version", Integer.class);
		this.mapField("createdByUser", AggregateState.BASE, "created_by_user_id", Integer.class);
		this.mapField("createdAt", AggregateState.BASE, "created_at", OffsetDateTime.class);
		this.mapField("modifiedByUser", AggregateState.BASE, "modified_by_user_id", Integer.class);
		this.mapField("modifiedAt", AggregateState.BASE, "modified_at", OffsetDateTime.class);
		this.mapField("closedByUser", AggregateState.BASE, "closed_by_user_id", Integer.class);
		this.mapField("closedAt", AggregateState.BASE, "closed_at", OffsetDateTime.class);
		this.mapCollection("transitionList", "obj.transitionList", ObjPartTransition.class);
	}

}
