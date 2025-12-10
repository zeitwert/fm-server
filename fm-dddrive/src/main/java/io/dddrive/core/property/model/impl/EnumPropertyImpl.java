package io.dddrive.core.property.model.impl;

import static io.dddrive.util.Invariant.assertThis;
import static io.dddrive.util.Invariant.requireThis;

import java.util.Objects;

import io.dddrive.core.enums.model.Enumerated;
import io.dddrive.core.enums.model.Enumeration;
import io.dddrive.core.property.model.EntityWithProperties;
import io.dddrive.core.property.model.EntityWithPropertiesSPI;
import io.dddrive.core.property.model.EnumProperty;
import io.dddrive.core.property.model.base.PropertyBase;

public class EnumPropertyImpl<E extends Enumerated> extends PropertyBase<E> implements EnumProperty<E> {

	private final Enumeration<E> enumeration;
	private String value;
	private final Class<E> enumType;

	public EnumPropertyImpl(EntityWithProperties entity, String name, Enumeration<E> enumeration, Class<E> enumType) {
		super(entity, name);
		this.enumeration = enumeration;
		this.enumType = enumType;
	}

	@Override
	public Enumeration<E> getEnumeration() {
		return this.enumeration;
	}

	@Override
	public E getValue() {
		return this.enumeration.getItem(value);
	}

	@Override
	public void setValue(E value) {
		requireThis(this.isWritable(), "not frozen");
		if (Objects.equals(this.getValue(), value)) {
			return;
		}
		assertThis(this.isValidEnum(value), () -> "valid enumeration item for " + this.enumeration.getId() + " (" + value.getId() + ")");
		EntityWithPropertiesSPI entity = (EntityWithPropertiesSPI) this.getEntity();
		entity.doBeforeSet(this, value, this.value);
		entity.fireFieldSetChange(this, value != null ? value.getId() : null, this.value);
		this.value = value != null ? value.getId() : null;
		entity.doAfterSet(this);
	}

	private boolean isValidEnum(E value) {
		if (value == null) {
			return true;
		} else if (!Objects.equals(value.getEnumeration(), this.enumeration)) {
			return false;
		}
		return Objects.equals(value, this.enumeration.getItem(value.getId()));
	}

	@Override
	public Class<E> getType() {
		return enumType;
	}

}
