package io.dddrive.jooq.ddd;

import org.jooq.UpdatableRecord;

import io.dddrive.ddd.model.base.AggregateSPI;
import io.dddrive.jooq.property.PropertyProviderMixin;
import io.dddrive.property.model.base.EntityWithPropertiesSPI;

public interface AggregatePropertyProviderMixin extends PropertyProviderMixin {

	@Override
	default UpdatableRecord<?> getDbRecord(EntityWithPropertiesSPI entity, String tableType) {
		Object state = ((AggregateSPI) entity).getAggregateState();
		if (AggregateState.EXTN.equals(tableType)) {
			return ((AggregateState) state).extnRecord();
		} else if (AggregateState.BASE.equals(tableType)) {
			return ((AggregateState) state).baseRecord();
		}
		return null;
	}

}
