package io.zeitwert.ddd.property.model.wrapper;

import io.zeitwert.ddd.property.model.SimpleProperty;
import io.zeitwert.ddd.property.model.base.EntityWithPropertiesSPI;

public class SimplePropertyWrapper<T> extends PropertyWrapperBase<T> implements SimpleProperty<T> {

	private SimpleProperty<T> property;

	public SimplePropertyWrapper(EntityWithPropertiesSPI entity, String name, Class<T> type) {
		super(entity, name, type);
	}

	@Override
	public SimpleProperty<T> getProperty() {
		if (this.property == null) {
			this.property = this.entity.getPropertyProvider().getSimpleProperty(this.entity, this.name, this.type);
		}
		return this.property;
	}

	@Override
	public T getValue() {
		return this.getProperty().getValue();
	}

	@Override
	public void setValue(T value) {
		this.getProperty().setValue(value);
	}

}
