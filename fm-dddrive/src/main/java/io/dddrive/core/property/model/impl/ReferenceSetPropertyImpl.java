package io.dddrive.core.property.model.impl;

import static io.dddrive.util.Invariant.assertThis;
import static io.dddrive.util.Invariant.requireThis;

import java.util.HashSet;
import java.util.Set;

import io.dddrive.core.ddd.model.Aggregate;
import io.dddrive.core.property.model.AggregateResolver;
import io.dddrive.core.property.model.EntityWithProperties;
import io.dddrive.core.property.model.EntityWithPropertiesSPI;
import io.dddrive.core.property.model.ReferenceSetProperty;
import io.dddrive.core.property.model.base.PropertyBase;

public class ReferenceSetPropertyImpl<A extends Aggregate> extends PropertyBase<A> implements ReferenceSetProperty<A> {

	private final Set<Object> itemSet = new HashSet<>();

	public ReferenceSetPropertyImpl(EntityWithProperties entity, String name, AggregateResolver<A> repository) {
		super(entity, name);
	}

	@Override
	public void clearItems() {
		requireThis(this.isWritable(), "not frozen");
		this.itemSet.forEach(this::removeItem);
		this.itemSet.clear();
		((EntityWithPropertiesSPI) this.getEntity()).doAfterClear(this);
	}

	@Override
	public void addItem(Object id) {
		requireThis(this.isWritable(), "not frozen");
		requireThis(id != null, "aggregateId not null");
		if (this.hasItem(id)) {
			return;
		}
		assertThis(this.isValidAggregateId(id), "valid aggregate id [" + id + "]");
		if (!this.hasItem(id)) {
			EntityWithPropertiesSPI entity = (EntityWithPropertiesSPI) this.getEntity();
			entity.fireValueAddedChange(this, id);
			this.itemSet.add(id);
			entity.doAfterAdd(this, null);
		}
	}

	@Override
	public Set<Object> getItems() {
		return Set.copyOf(this.itemSet);
	}

	@Override
	public boolean hasItem(Object aggregateId) {
		return this.itemSet.contains(aggregateId);
	}

	@Override
	public void removeItem(Object aggregateId) {
		requireThis(this.isWritable(), "not frozen");
		requireThis(aggregateId != null, "aggregateId not null");
		if (this.hasItem(aggregateId)) {
			EntityWithPropertiesSPI entity = (EntityWithPropertiesSPI) this.getEntity();
			entity.fireValueRemovedChange(this, aggregateId);
			this.itemSet.remove(aggregateId);
			entity.doAfterRemove(this);
		}
	}

	// TODO too expensive?
	private boolean isValidAggregateId(Object id) {
		return true;
	}

}
