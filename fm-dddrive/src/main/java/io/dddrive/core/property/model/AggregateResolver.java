package io.dddrive.core.property.model;

import io.dddrive.core.ddd.model.Aggregate;

public interface AggregateResolver<A extends Aggregate> {
	A get(Object id);
}
