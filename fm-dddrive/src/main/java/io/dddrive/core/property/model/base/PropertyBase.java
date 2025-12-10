package io.dddrive.core.property.model.base;

import io.dddrive.core.property.model.EntityWithProperties;
import io.dddrive.core.property.model.EntityWithPropertiesSPI;
import io.dddrive.core.property.model.Property;

public abstract class PropertyBase<T> implements Property<T> {

	private final EntityWithProperties entity;
	private final String name;

	protected PropertyBase(EntityWithProperties entity, String name) {
		this.entity = entity;
		this.name = name;
	}

	@Override
	public EntityWithProperties getEntity() {
		return this.entity;
	}

	@Override
	public String getRelativePath() {
		String relativePath = ((EntityWithPropertiesSPI) this.entity).getRelativePath();
		return relativePath.isEmpty() ? this.name : relativePath + "." + this.name;
	}

	@Override
	public String getPath() {
		return ((EntityWithPropertiesSPI) this.entity).getPath() + "." + this.name;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public boolean isWritable() {
		return !this.getEntity().isFrozen();
	}

}
