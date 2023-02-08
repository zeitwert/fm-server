package io.zeitwert.jooq.property;

import io.zeitwert.jooq.persistence.PartState;

public interface DocPartPropertyProviderMixin extends PartPropertyProviderMixin {

	@Override
	default void mapProperties() {
		PartPropertyProviderMixin.super.mapProperties();
		this.mapField("docId", PartState.BASE, "doc_id", Integer.class);
	}

}
