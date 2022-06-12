package io.zeitwert.ddd.property.model.impl;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.property.model.ReferenceProperty;
import io.zeitwert.ddd.property.model.base.EntityWithPropertiesSPI;
import io.zeitwert.ddd.property.model.base.PropertyBase;

import java.util.Objects;

import org.jooq.Field;
import org.jooq.UpdatableRecord;

import static io.zeitwert.ddd.util.Check.assertThis;

public class ReferencePropertyImpl<A extends Aggregate> extends PropertyBase<A> implements ReferenceProperty<A> {

	private final UpdatableRecord<?> dbRecord;
	private final Field<Integer> field;
	private final AggregateRepository<A, ?> repository;

	public ReferencePropertyImpl(EntityWithPropertiesSPI entity, UpdatableRecord<?> dbRecord, Field<Integer> field,
			AggregateRepository<A, ?> repository) {
		super(entity);
		this.dbRecord = dbRecord;
		this.field = field;
		this.repository = repository;
	}

	@Override
	public String getName() {
		return this.field.getName();
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
		assertThis(this.isValidAggregateId(id),
				"valid aggregate id [" + this.repository.getAggregateType().getId() + ": " + id + "]");
		this.dbRecord.setValue(this.field, id);
		this.getEntity().afterSet(this);
	}

	@Override
	public A getValue() {
		return this.getId() == null ? null
				: this.repository.get(this.getEntity().getMeta().getSessionInfo(), this.getId());
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
