package fm.comunas.ddd.property.model.base;

import fm.comunas.ddd.property.model.Property;

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

}
