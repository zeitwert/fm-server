package io.zeitwert.ddd.persistence.jooq.impl;

import io.zeitwert.ddd.enums.model.Enumerated;
import io.zeitwert.ddd.enums.model.Enumeration;
import io.zeitwert.ddd.property.model.EnumProperty;
import io.zeitwert.ddd.property.model.base.EntityWithPropertiesSPI;
import io.zeitwert.ddd.property.model.base.PropertyBase;

import java.util.Objects;

import org.jooq.Field;
import org.jooq.UpdatableRecord;

import static io.zeitwert.ddd.util.Check.assertThis;

public class EnumPropertyImpl<E extends Enumerated> extends PropertyBase<E> implements EnumProperty<E> {

	private final UpdatableRecord<?> dbRecord;
	private final Field<String> field;
	private final Enumeration<E> enumeration;

	public EnumPropertyImpl(EntityWithPropertiesSPI entity, UpdatableRecord<?> dbRecord, Field<String> field,
			Enumeration<E> enumeration) {
		super(entity);
		this.dbRecord = dbRecord;
		this.field = field;
		this.enumeration = enumeration;
	}

	@Override
	public String getName() {
		return this.field.getName();
	}

	@Override
	public E getValue() {
		String enumId = this.dbRecord.getValue(this.field);
		return this.enumeration.getItem(enumId);
	}

	@Override
	public void setValue(E value) {
		assertThis(this.isWritable(), "writable");
		if (Objects.equals(this.getValue(), value)) {
			return;
		}
		assertThis(this.isValidEnum(value),
				"valid enumeration item [" + this.enumeration.getId() + ": " + value + "]");
		this.dbRecord.setValue(this.field, value != null ? value.getId() : null);
		this.getEntity().afterSet(this);
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
