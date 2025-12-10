package io.dddrive.core.property.model.base;

import static io.dddrive.util.Invariant.assertThis;
import static io.dddrive.util.Invariant.requireThis;

import java.util.Objects;

import io.dddrive.core.property.model.BaseProperty;
import io.dddrive.core.property.model.EntityWithProperties;
import io.dddrive.core.property.model.EntityWithPropertiesSPI;

public abstract class ReferencePropertyBase<E, ID> extends PropertyBase<E> {

	private static final String ID_PROPERTY_NAME = "id";
	private ID id;
	private final BaseProperty<ID> idProperty;
	private final Class<ID> idType;

	public ReferencePropertyBase(EntityWithProperties entity, String name, Class<ID> idType) {
		super(entity, name);
		this.idType = idType;
		this.idProperty = new IdProperty();
	}

	public ID getId() {
		return id;
	}

	public void setId(ID id) {
		requireThis(this.isWritable(), "not frozen");
		if (Objects.equals(this.getId(), id)) {
			return;
		}
		assertThis(isValidId(id), "valid id [" + id + "]");
		EntityWithPropertiesSPI entity = (EntityWithPropertiesSPI) this.getEntity();
		entity.doBeforeSet(this, id, this.id);
		entity.fireFieldSetChange(this, id, this.id);
		this.id = id;
		entity.doAfterSet(this);
	}

	public BaseProperty<ID> getIdProperty() {
		return idProperty;
	}

	protected abstract boolean isValidId(ID id);

	private class IdProperty implements BaseProperty<ID> {
		@Override
		public EntityWithProperties getEntity() {
			return ReferencePropertyBase.this.getEntity();
		}

		@Override
		public String getRelativePath() {
			String relativePath = ReferencePropertyBase.this.getRelativePath();
			return relativePath.isEmpty() ? ID_PROPERTY_NAME : relativePath + "." + ID_PROPERTY_NAME;
		}

		@Override
		public String getPath() {
			return ReferencePropertyBase.this.getPath() + "." + ID_PROPERTY_NAME;
		}

		@Override
		public String getName() {
			return ID_PROPERTY_NAME;
		}

		@Override
		public boolean isWritable() {
			return ReferencePropertyBase.this.isWritable();
		}

		@Override
		public void setValue(ID value) {
			ReferencePropertyBase.this.setId(value);
		}

		@Override
		public ID getValue() {
			return ReferencePropertyBase.this.getId();
		}

		@Override
		public Class<ID> getType() {
			return idType;
		}
	}
}