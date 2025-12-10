package io.dddrive.core.property.model.impl;

import static io.dddrive.util.Invariant.assertThis;
import static io.dddrive.util.Invariant.requireThis;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import io.dddrive.core.enums.model.Enumerated;
import io.dddrive.core.enums.model.Enumeration;
import io.dddrive.core.property.model.EntityWithProperties;
import io.dddrive.core.property.model.EntityWithPropertiesSPI;
import io.dddrive.core.property.model.EnumSetProperty;
import io.dddrive.core.property.model.base.PropertyBase;

public class EnumSetPropertyImpl<E extends Enumerated> extends PropertyBase<E> implements EnumSetProperty<E> {

	private final Enumeration<E> enumeration;

	private final Set<E> itemSet = new HashSet<>();

	public EnumSetPropertyImpl(EntityWithProperties entity, String name, Enumeration<E> enumeration) {
		super(entity, name);
		this.enumeration = enumeration;
	}

	@Override
	public void clearItems() {
		requireThis(this.isWritable(), "not frozen");
		this.itemSet.forEach(this::removeItem);
		this.itemSet.clear();
		((EntityWithPropertiesSPI) this.getEntity()).doAfterClear(this);
	}

	@Override
	public void addItem(E item) {
		requireThis(this.isWritable(), "not frozen");
		requireThis(item != null, "item not null");
		assertThis(this.isValidEnum(item), () -> "valid enumeration item for " + this.enumeration.getId() + " (" + item.getId() + ")");
		if (!this.hasItem(item)) {
			EntityWithPropertiesSPI entity = (EntityWithPropertiesSPI) this.getEntity();
			entity.fireValueAddedChange(this, item.getId());
			this.itemSet.add(item);
			entity.doAfterAdd(this, null);
		}
	}

	@Override
	public Set<E> getItems() {
		return Set.copyOf(this.itemSet);
	}

	@Override
	public boolean hasItem(E item) {
		return this.itemSet.contains(item);
	}

	@Override
	public void removeItem(E item) {
		requireThis(this.isWritable(), "not frozen");
		requireThis(item != null, "item not null");
		if (this.hasItem(item)) {
			EntityWithPropertiesSPI entity = (EntityWithPropertiesSPI) this.getEntity();
			entity.fireValueRemovedChange(this, item.getId());
			this.itemSet.remove(item);
			entity.doAfterRemove(this);
		}
	}

	private boolean isValidEnum(E value) {
		if (value == null) {
			return true;
		} else if (!Objects.equals(value.getEnumeration(), this.enumeration)) {
			return false;
		}
		return Objects.equals(value, this.enumeration.getItem(value.getId()));
	}

}
