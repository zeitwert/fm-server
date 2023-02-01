package io.zeitwert.ddd.property.model.wrapper;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.property.model.ReferenceProperty;
import io.zeitwert.ddd.property.model.base.EntityWithPropertiesSPI;

public class ReferencePropertyWrapper<A extends Aggregate> extends PropertyWrapperBase<A>
		implements ReferenceProperty<A> {

	private ReferenceProperty<A> property;

	public ReferencePropertyWrapper(EntityWithPropertiesSPI entity, String name, Class<A> type) {
		super(entity, name, type);
	}

	@Override
	public ReferenceProperty<A> getProperty() {
		if (this.property == null) {
			this.property = this.entity.getPropertyProvider().getReferenceProperty(this.entity, this.name, this.type);
		}
		return this.property;
	}

	@Override
	public Integer getId() {
		return this.getProperty().getId();
	}

	@Override
	public void setId(Integer id) {
		this.getProperty().setId(id);
	}

	@Override
	public A getValue() {
		return this.getProperty().getValue();
	}

	@Override
	public void setValue(A value) {
		this.getProperty().setValue(value);
	}

}
