package io.zeitwert.ddd.property.model;

import io.zeitwert.ddd.aggregate.model.Aggregate;

public interface AggregateResolver<A extends Aggregate> {
	A get(Integer id);
}
