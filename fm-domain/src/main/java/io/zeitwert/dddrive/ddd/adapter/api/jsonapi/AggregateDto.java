package io.zeitwert.dddrive.ddd.adapter.api.jsonapi;

import io.dddrive.core.ddd.model.Aggregate;

public interface AggregateDto<A extends Aggregate> {

	AggregateDtoAdapter<?, ?> getAdapter();

}
