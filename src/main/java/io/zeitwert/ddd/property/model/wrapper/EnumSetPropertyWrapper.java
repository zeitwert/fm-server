package io.zeitwert.ddd.property.model.wrapper;

import java.util.Collection;
import java.util.Set;

import io.zeitwert.ddd.enums.model.Enumerated;
import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.property.model.AggregatePartItem;
import io.zeitwert.ddd.property.model.EnumSetProperty;
import io.zeitwert.ddd.property.model.base.EntityWithPropertiesSPI;

public class EnumSetPropertyWrapper<E extends Enumerated> extends PropertyWrapperBase<E> implements EnumSetProperty<E> {

	private EnumSetProperty<E> property;

	public EnumSetPropertyWrapper(EntityWithPropertiesSPI entity, String name, Class<E> type) {
		super(entity, name, type);
	}

	@Override
	public EnumSetProperty<E> getProperty() {
		if (this.property == null) {
			this.property = this.entity.getPropertyProvider().getEnumSetProperty(this.entity, this.name, this.type);
		}
		return this.property;
	}

	@Override
	public CodePartListType getPartListType() {
		return this.getProperty().getPartListType();
	}

	@Override
	public Set<E> getItems() {
		return this.getProperty().getItems();
	}

	@Override
	public boolean hasItem(E item) {
		return this.getProperty().hasItem(item);
	}

	@Override
	public void clearItems() {
		this.getProperty().clearItems();
	}

	@Override
	public void addItem(E item) {
		this.getProperty().addItem(item);
	}

	@Override
	public void removeItem(E item) {
		this.getProperty().removeItem(item);
	}

	@Override
	public void loadEnums(Collection<? extends AggregatePartItem<?>> enums) {
		this.getProperty().loadEnums(enums);
	}

}
