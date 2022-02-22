package fm.comunas.ddd.property.model.impl;

import fm.comunas.ddd.aggregate.model.Aggregate;
import fm.comunas.ddd.aggregate.model.AggregateRepository;
import fm.comunas.ddd.property.model.ReferenceProperty;
import fm.comunas.ddd.property.model.base.EntityWithPropertiesSPI;
import fm.comunas.ddd.property.model.base.PropertyBase;

import java.util.Objects;

import org.jooq.Field;
import org.jooq.UpdatableRecord;
import org.springframework.util.Assert;

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
		Assert.isTrue(this.isWritable(), "writable");
		if (Objects.equals(this.getId(), id)) {
			return;
		}
		Assert.isTrue(this.isValidAggregateId(id),
				"valid aggregate id [" + this.repository.getAggregateType().getId() + ": " + id + "]");
		this.dbRecord.setValue(this.field, id);
		this.getEntity().afterSet(this);
	}

	@Override
	public A getValue() {
		return this.getId() == null ? null
				: this.repository.get(this.getEntity().getMeta().getSessionInfo(), this.getId()).orElse(null);
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
