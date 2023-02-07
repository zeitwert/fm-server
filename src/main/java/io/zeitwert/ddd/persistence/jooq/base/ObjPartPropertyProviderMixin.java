package io.zeitwert.ddd.persistence.jooq.base;

import io.zeitwert.ddd.persistence.jooq.PartState;

public interface ObjPartPropertyProviderMixin extends PartPropertyProviderMixin {

	@Override
	default void mapFields() {
		PartPropertyProviderMixin.super.mapFields();
		this.mapField("objId", PartState.BASE, "obj_id", Integer.class);
	}

}
