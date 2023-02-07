package io.zeitwert.ddd.persistence.jooq.base;

import io.zeitwert.ddd.persistence.jooq.PartState;

public interface DocPartPropertyProviderMixin extends PartPropertyProviderMixin {

	@Override
	default void mapFields() {
		PartPropertyProviderMixin.super.mapFields();
		this.mapField("docId", PartState.BASE, "doc_id", Integer.class);
	}

}
