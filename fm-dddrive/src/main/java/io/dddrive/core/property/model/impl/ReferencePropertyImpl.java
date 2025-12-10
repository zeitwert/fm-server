package io.dddrive.core.property.model.impl;

import io.dddrive.core.ddd.model.Aggregate;
import io.dddrive.core.property.model.AggregateResolver;
import io.dddrive.core.property.model.EntityWithProperties;
import io.dddrive.core.property.model.ReferenceProperty;
import io.dddrive.core.property.model.base.ReferencePropertyBase;

public class ReferencePropertyImpl<A extends Aggregate> extends ReferencePropertyBase<A, Object>
	implements ReferenceProperty<A> {

	private final AggregateResolver<A> resolver;
	private final Class<A> aggregateType;

	public ReferencePropertyImpl(EntityWithProperties entity, String name, AggregateResolver<A> resolver, Class<A> aggregateType) {
		super(entity, name, Object.class);
		this.resolver = resolver;
		this.aggregateType = aggregateType;
	}

	@Override
	public A getValue() {
		return getId() == null ? null : this.resolver.get(getId());
	}

	@Override
	public void setValue(A value) {
		setId(value == null ? null : value.getId());
	}

	@Override
	protected boolean isValidId(Object id) {
		// TODO too expensive?
		return true;
	}

	@Override
	public Class<A> getType() {
		return aggregateType;
	}
}
