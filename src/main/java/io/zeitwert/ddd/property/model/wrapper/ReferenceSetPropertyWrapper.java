package io.zeitwert.ddd.property.model.wrapper;

import java.util.Collection;
import java.util.Set;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.property.model.AggregatePartItem;
import io.zeitwert.ddd.property.model.ReferenceSetProperty;
import io.zeitwert.ddd.property.model.base.EntityWithPropertiesSPI;

public class ReferenceSetPropertyWrapper<A extends Aggregate> extends PropertyWrapperBase<A>
		implements ReferenceSetProperty<A> {

	private ReferenceSetProperty<A> property;

	public ReferenceSetPropertyWrapper(EntityWithPropertiesSPI entity, String name, Class<A> type) {
		super(entity, name, type);
	}

	@Override
	public ReferenceSetProperty<A> getProperty() {
		if (this.property == null) {
			this.property = this.entity.getPropertyProvider().getReferenceSetProperty(this.entity, this.name, this.type);
		}
		return this.property;
	}

	@Override
	public CodePartListType getPartListType() {
		return this.getProperty().getPartListType();
	}

	@Override
	public Set<Integer> getItems() {
		return this.getProperty().getItems();
	}

	@Override
	public boolean hasItem(Integer aggregateId) {
		return this.getProperty().hasItem(aggregateId);
	}

	@Override
	public void clearItems() {
		this.getProperty().clearItems();
	}

	@Override
	public void addItem(Integer aggregateId) {
		this.getProperty().addItem(aggregateId);
	}

	@Override
	public void removeItem(Integer aggregateId) {
		this.getProperty().removeItem(aggregateId);
	}

	@Override
	public void loadReferences(Collection<? extends AggregatePartItem<?>> items) {
		this.getProperty().loadReferences(items);
	}

}
