package io.zeitwert.ddd.persistence.jooq.impl;

import static io.zeitwert.ddd.util.Check.assertThis;

import java.util.Objects;

import org.jooq.Field;
import org.jooq.UpdatableRecord;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.property.model.AggregateResolver;
import io.zeitwert.ddd.property.model.ReferenceProperty;
import io.zeitwert.ddd.property.model.base.EntityWithPropertiesSPI;
import io.zeitwert.ddd.property.model.base.PropertyBase;

public class ReferencePropertyImpl<A extends Aggregate> extends PropertyBase<A> implements ReferenceProperty<A> {

	private final UpdatableRecord<?> dbRecord;
	private final String name;
	private final Field<Integer> field;
	private final AggregateResolver<A> resolver;

	public ReferencePropertyImpl(EntityWithPropertiesSPI entity, UpdatableRecord<?> dbRecord, String name,
			Field<Integer> field, AggregateResolver<A> resolver) {
		super(entity);
		this.dbRecord = dbRecord;
		this.name = name;
		this.field = field;
		this.resolver = resolver;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public Integer getId() {
		return this.dbRecord.getValue(this.field);
	}

	@Override
	public void setId(Integer id) {
		assertThis(this.isWritable(), "writable");
		if (Objects.equals(this.getId(), id)) {
			return;
		}
		assertThis(this.isValidAggregateId(id), "valid aggregate id [" + id + "]");
		this.dbRecord.setValue(this.field, id);
		this.getEntity().afterSet(this);
	}

	@Override
	public A getValue() {
		return this.getId() == null ? null : this.resolver.get(this.getId());
	}

	@Override
	public void setValue(A value) {
		this.setId(value == null ? null : value.getId());
	}

	// TODO too expensive?
	private boolean isValidAggregateId(Integer id) {
		return true;
	}

}
