package io.zeitwert.jooq.property;

import io.zeitwert.jooq.persistence.PartState;

public interface ObjPartPropertyProviderMixin extends PartPropertyProviderMixin {

	@Override
	default void mapFields() {
		PartPropertyProviderMixin.super.mapFields();
		this.mapField("objId", PartState.BASE, "obj_id", Integer.class);
	}

}
