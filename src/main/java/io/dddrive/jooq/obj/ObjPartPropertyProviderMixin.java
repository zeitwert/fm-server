package io.dddrive.jooq.obj;

import io.dddrive.jooq.ddd.PartPropertyProviderMixin;
import io.dddrive.jooq.ddd.PartState;

public interface ObjPartPropertyProviderMixin extends PartPropertyProviderMixin {

	@Override
	default void mapProperties() {
		PartPropertyProviderMixin.super.mapProperties();
		this.mapField("objId", PartState.BASE, "obj_id", Integer.class);
	}

}
