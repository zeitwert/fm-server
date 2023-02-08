package io.zeitwert.jooq.property;

import io.zeitwert.jooq.persistence.AggregateState;

public interface ObjExtnPropertyProviderMixin
		extends ObjPropertyProviderMixin {

	@Override
	default void mapProperties() {
		ObjPropertyProviderMixin.super.mapProperties();
		this.mapField("extnObjId", AggregateState.EXTN, "obj_id", Integer.class);
		this.mapField("extnTenantId", AggregateState.EXTN, "tenant_id", Integer.class);
		if (this.hasAccount()) {
			this.mapField("extnAccountId", AggregateState.EXTN, "account_id", Integer.class);
		} else {
			this.mapField("extnAccountId", AggregateState.BASE, "account_id", Integer.class);
		}
	}

	default boolean hasAccount() {
		return true;
	}

}
