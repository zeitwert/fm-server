package io.zeitwert.jooq.property;

import io.zeitwert.jooq.persistence.AggregateState;

public interface DocExtnPropertyProviderMixin
		extends DocPropertyProviderMixin {

	@Override
	default void mapProperties() {
		DocPropertyProviderMixin.super.mapProperties();
		this.mapField("extnDocId", AggregateState.EXTN, "doc_id", Integer.class);
		this.mapField("extnTenantId", AggregateState.EXTN, "tenant_id", Integer.class);
		this.mapField("extnAccountId", AggregateState.EXTN, "account_id", Integer.class);
	}

}
