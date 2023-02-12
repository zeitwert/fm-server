package io.dddrive.property.model;

import io.dddrive.ddd.model.Aggregate;

public interface AggregateResolver<A extends Aggregate> {
	A get(Integer id);
}
