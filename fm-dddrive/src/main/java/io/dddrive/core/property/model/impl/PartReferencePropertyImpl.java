package io.dddrive.core.property.model.impl;

import static io.dddrive.util.Invariant.assertThis;
import static io.dddrive.util.Invariant.requireThis;

import java.util.Objects;

import io.dddrive.core.ddd.model.Part;
import io.dddrive.core.property.model.EntityWithProperties;
import io.dddrive.core.property.model.EntityWithPropertiesSPI;
import io.dddrive.core.property.model.PartReferenceProperty;
import io.dddrive.core.property.model.PartResolver;
import io.dddrive.core.property.model.base.ReferencePropertyBase;

public class PartReferencePropertyImpl<P extends Part<?>> extends ReferencePropertyBase<P, Integer> implements PartReferenceProperty<P> {

	private final PartResolver<P> resolver;
	private Integer id;
	private final Class<P> partType;

	public PartReferencePropertyImpl(EntityWithProperties entity, String name, PartResolver<P> resolver, Class<P> partType) {
		super(entity, name, Integer.class);
		this.resolver = resolver;
		this.partType = partType;
	}

	@Override
	public Integer getId() {
		return this.id;
	}

	@Override
	public void setId(Integer id) {
		requireThis(this.isWritable(), "not frozen");
		if (Objects.equals(this.getId(), id)) {
			return;
		}
		assertThis(this.isValidId(id), "valid part id [" + id + "]");
		EntityWithPropertiesSPI entity = (EntityWithPropertiesSPI) this.getEntity();
		entity.doBeforeSet(this, id, this.id);
		entity.fireFieldSetChange(this, id, this.id);
		this.id = id;
		entity.doAfterSet(this);
	}

	@Override
	public P getValue() {
		return this.getId() == null ? null : this.resolver.get(this.getId());
	}

	@Override
	public void setValue(P value) {
		this.setId(value == null ? null : value.getId());
	}

	@Override
	protected boolean isValidId(Integer id) {
		return id == null || this.resolver.get(id) != null;
	}

	@Override
	public Class<P> getType() {
		return partType;
	}
}
