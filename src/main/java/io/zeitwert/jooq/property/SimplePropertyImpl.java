package io.zeitwert.jooq.property;

import io.zeitwert.ddd.property.model.SimpleProperty;
import io.zeitwert.ddd.property.model.base.EntityWithPropertiesSPI;
import io.zeitwert.ddd.property.model.base.PropertyBase;

import java.util.Objects;

import org.jooq.Field;
import org.jooq.UpdatableRecord;

import static io.zeitwert.ddd.util.Check.assertThis;

public class SimplePropertyImpl<T> extends PropertyBase<T> implements SimpleProperty<T> {

	private final UpdatableRecord<?> dbRecord;
	private final String fieldName;
	private final Field<T> field;

	public SimplePropertyImpl(EntityWithPropertiesSPI entity, UpdatableRecord<?> dbRecord, Field<T> field) {
		super(entity);
		this.dbRecord = dbRecord;
		this.fieldName = null;
		this.field = field;
	}

	public SimplePropertyImpl(EntityWithPropertiesSPI entity, UpdatableRecord<?> dbRecord, String fieldName,
			Field<T> field) {
		super(entity);
		this.dbRecord = dbRecord;
		this.fieldName = fieldName;
		this.field = field;
	}

	@Override
	public String getName() {
		return this.fieldName != null ? this.fieldName : this.field.getName();
	}

	@Override
	public T getValue() {
		return this.dbRecord.getValue(this.field);
	}

	@Override
	public void setValue(T value) {
		assertThis(this.isWritable(), "writable");
		if (Objects.equals(this.getValue(), value)) {
			return;
		}
		this.dbRecord.setValue(this.field, value);
		this.getEntity().afterSet(this);
	}

}
