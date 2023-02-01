package io.zeitwert.ddd.db.model.wrapper;

import io.zeitwert.ddd.enums.model.Enumerated;
import io.zeitwert.ddd.property.model.EnumProperty;
import io.zeitwert.ddd.property.model.base.EntityWithPropertiesSPI;

public class EnumPropertyWrapper<E extends Enumerated> extends PropertyWrapperBase<E> implements EnumProperty<E> {

	private EnumProperty<E> property;

	public EnumPropertyWrapper(EntityWithPropertiesSPI entity, String name, Class<E> type) {
		super(entity, name, type);
	}

	@Override
	public EnumProperty<E> getProperty() {
		if (this.property == null) {
			this.property = this.entity.getPersistenceProvider().getEnumProperty(this.entity, this.name, this.type);
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
