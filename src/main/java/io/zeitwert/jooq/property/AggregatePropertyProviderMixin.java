package io.zeitwert.jooq.property;

import org.jooq.UpdatableRecord;

import io.zeitwert.ddd.aggregate.model.base.AggregateSPI;
import io.zeitwert.ddd.property.model.base.EntityWithPropertiesSPI;
import io.zeitwert.jooq.persistence.AggregateState;

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
