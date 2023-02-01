package io.zeitwert.ddd.obj.model.base;

import java.time.OffsetDateTime;

import io.zeitwert.ddd.aggregate.model.base.JooqPropertyProviderBase;
import io.zeitwert.ddd.obj.model.ObjPartTransition;

public /* TODO abstract */ class ObjPropertyProviderBase extends JooqPropertyProviderBase {

	@Override
	public Class<?> getEntityClass() {
		return null;
	}

	public ObjPropertyProviderBase() {
		super();
		this.mapField("id", DbTableType.BASE, "id", Integer.class);
		this.mapField("objTypeId", DbTableType.BASE, "obj_type_id", String.class);
		this.mapField("tenant", DbTableType.BASE, "tenant_id", Integer.class);
		this.mapField("owner", DbTableType.BASE, "owner_id", Integer.class);
		this.mapField("caption", DbTableType.BASE, "caption", String.class);
		this.mapField("version", DbTableType.BASE, "version", Integer.class);
		this.mapField("createdByUser", DbTableType.BASE, "created_by_user_id", Integer.class);
		this.mapField("createdAt", DbTableType.BASE, "created_at", OffsetDateTime.class);
		this.mapField("modifiedByUser", DbTableType.BASE, "modified_by_user_id", Integer.class);
		this.mapField("modifiedAt", DbTableType.BASE, "modified_at", OffsetDateTime.class);
		this.mapField("closedByUser", DbTableType.BASE, "closed_by_user_id", Integer.class);
		this.mapField("closedAt", DbTableType.BASE, "closed_at", OffsetDateTime.class);
		this.mapCollection("transitionList", "obj.transitionList", ObjPartTransition.class);
	}

}
