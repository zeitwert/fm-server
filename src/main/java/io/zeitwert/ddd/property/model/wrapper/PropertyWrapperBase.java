package io.zeitwert.ddd.property.model.wrapper;

import io.zeitwert.ddd.property.model.Property;
import io.zeitwert.ddd.property.model.PropertyWrapper;
import io.zeitwert.ddd.property.model.base.EntityWithPropertiesSPI;

public abstract class PropertyWrapperBase<T> implements Property<T>, PropertyWrapper<T> {

	protected final EntityWithPropertiesSPI entity;
	protected final String name;
	protected final Class<T> type;

	public PropertyWrapperBase(EntityWithPropertiesSPI entity, String name, Class<T> type) {
		this.entity = entity;
		this.name = name;
		this.type = type;
	}

	@Override
	public abstract Property<T> getProperty();

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public boolean isWritable() {
		return this.getProperty().isWritable();
	}

	@Override
	public void setWritable(boolean isWritable) {
		this.getProperty().setWritable(isWritable);
	}

	@Override
	public boolean equals(Object other) {
		return this.getProperty().equals(other);
	}

}
