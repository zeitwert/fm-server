package io.dddrive.property.model.wrapper;

import io.dddrive.enums.model.Enumerated;
import io.dddrive.property.model.EnumProperty;
import io.dddrive.property.model.base.EntityWithPropertiesSPI;

public class EnumPropertyWrapper<E extends Enumerated> extends PropertyWrapperBase<E> implements EnumProperty<E> {

	private EnumProperty<E> property;

	public EnumPropertyWrapper(EntityWithPropertiesSPI entity, String name, Class<E> type) {
		super(entity, name, type);
	}

	@Override
	public EnumProperty<E> getProperty() {
		if (this.property == null) {
			this.property = this.entity.getPropertyProvider().getEnumProperty(this.entity, this.name, this.type);
		}
		return this.property;
	}

	@Override
	public E getValue() {
		return this.getProperty().getValue();
	}

	@Override
	public void setValue(E value) {
		this.getProperty().setValue(value);
	}

}
