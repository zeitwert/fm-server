package fm.comunas.ddd.property.model.impl;

import fm.comunas.ddd.property.model.SimpleProperty;
import fm.comunas.ddd.property.model.base.EntityWithPropertiesSPI;
import fm.comunas.ddd.property.model.base.PropertyBase;

import java.util.Objects;

import org.jooq.Field;
import org.jooq.UpdatableRecord;
import org.springframework.util.Assert;

public class SimplePropertyImpl<T> extends PropertyBase<T> implements SimpleProperty<T> {

	private final UpdatableRecord<?> dbRecord;
	private final Field<T> field;

	public SimplePropertyImpl(EntityWithPropertiesSPI entity, UpdatableRecord<?> dbRecord, Field<T> field) {
		super(entity);
		this.dbRecord = dbRecord;
		this.field = field;
	}

	@Override
	public String getName() {
		return this.field.getName();
	}

	@Override
	public T getValue() {
		return this.dbRecord.getValue(this.field);
	}

	@Override
	public void setValue(T value) {
		Assert.isTrue(this.isWritable(), "writable");
		if (Objects.equals(this.getValue(), value)) {
			return;
		}
		this.dbRecord.setValue(this.field, value);
		this.getEntity().afterSet(this);
	}

}
