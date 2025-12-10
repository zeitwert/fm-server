package io.dddrive.core.property.model.impl;

import static io.dddrive.util.Invariant.requireThis;

import java.util.Objects;

import io.dddrive.core.property.model.BaseProperty;
import io.dddrive.core.property.model.EntityWithProperties;
import io.dddrive.core.property.model.EntityWithPropertiesSPI;
import io.dddrive.core.property.model.base.PropertyBase;

public class BasePropertyImpl<T> extends PropertyBase<T> implements BaseProperty<T> {

	private T value;
	private final Class<T> type;

	public BasePropertyImpl(EntityWithProperties entity, String name, Class<T> type) {
		super(entity, name);
		this.type = type;
	}

	@Override
	public T getValue() {
		return this.value;
	}

	@Override
	public void setValue(T value) {
		requireThis(this.isWritable(), "not frozen");
		if (Objects.equals(this.value, value)) {
			return;
		}
		T oldValue = this.value;
		EntityWithPropertiesSPI entity;
		// TODO separate handling of setting id needs to be reviewed
		if (this.getName().equals("id")) {
			// id field needs to be set before fireFieldSetChange, to get the correct path
			this.value = value;
			entity = (EntityWithPropertiesSPI) this.getEntity();
		} else {
			entity = (EntityWithPropertiesSPI) this.getEntity();
			entity.doBeforeSet(this, value, oldValue);
			this.value = value;
		}
		entity.fireFieldSetChange(this, value, oldValue);
		entity.doAfterSet(this);
	}

	@Override
	public Class<T> getType() {
		return this.type;
	}

}
