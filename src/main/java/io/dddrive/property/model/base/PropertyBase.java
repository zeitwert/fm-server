package io.dddrive.property.model.base;

import io.dddrive.property.model.Property;
import io.dddrive.property.model.PropertyWrapper;

public abstract class PropertyBase<T> implements Property<T> {

	private final EntityWithPropertiesSPI entity;
	private boolean isWritable = true;

	protected PropertyBase(EntityWithPropertiesSPI entity) {
		this.entity = entity;
	}

	protected EntityWithPropertiesSPI getEntity() {
		return this.entity;
	}

	@Override
	public abstract String getName();

	@Override
	public boolean isWritable() {
		return this.isWritable;
	}

	@Override
	public void setWritable(boolean isWritable) {
		this.isWritable = isWritable;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		} else if (other == null) {
			return false;
		} else if (this.getClass() == other.getClass()) {
			return false;
		} else if (PropertyWrapper.class.isAssignableFrom(other.getClass())) {
			PropertyWrapper<?> that = (PropertyWrapper<?>) other;
			return this == that.getProperty();
		}
		return false;
	}

}
