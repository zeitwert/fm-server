package io.dddrive.jooq.doc;

import io.dddrive.jooq.ddd.PartPropertyProviderMixin;
import io.dddrive.jooq.ddd.PartState;

public interface DocPartPropertyProviderMixin extends PartPropertyProviderMixin {

	@Override
	default void mapProperties() {
		PartPropertyProviderMixin.super.mapProperties();
		this.mapField("docId", PartState.BASE, "doc_id", Integer.class);
	}

}
