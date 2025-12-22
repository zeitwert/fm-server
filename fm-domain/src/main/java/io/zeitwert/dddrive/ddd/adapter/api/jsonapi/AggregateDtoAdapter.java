package io.zeitwert.dddrive.ddd.adapter.api.jsonapi;

import io.dddrive.ddd.model.Aggregate;

public interface AggregateDtoAdapter<A extends Aggregate, D extends AggregateDto<A>> {

	void toAggregate(D dto, A aggregate);

	D fromAggregate(A aggregate);

}
