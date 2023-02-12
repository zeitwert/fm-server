package io.dddrive.jooq.ddd;

import org.jooq.UpdatableRecord;

import io.dddrive.ddd.model.base.PartSPI;
import io.dddrive.jooq.property.PropertyProviderMixin;
import io.dddrive.property.model.base.EntityWithPropertiesSPI;

public interface PartPropertyProviderMixin extends PropertyProviderMixin {

	@Override
	default void mapProperties() {
		this.mapField("id", PartState.BASE, "id", Integer.class);
		this.mapField("parentPartId", PartState.BASE, "parent_part_id", Integer.class);
		this.mapField("partListTypeId", PartState.BASE, "part_list_type_id", String.class);
		this.mapField("seqNr", PartState.BASE, "seq_nr", Integer.class);
	}

	@Override
	default UpdatableRecord<?> getDbRecord(EntityWithPropertiesSPI entity, String tableType) {
		Object state = ((PartSPI<?>) entity).getPartState();
		return ((PartState) state).dbRecord();
	}

}
