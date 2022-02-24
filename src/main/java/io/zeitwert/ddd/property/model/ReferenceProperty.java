package io.zeitwert.ddd.property.model;

import io.zeitwert.ddd.aggregate.model.Aggregate;

public interface ReferenceProperty<A extends Aggregate> extends SimpleProperty<A> {

	Integer getId();

	void setId(Integer id);

	@Override
	A getValue();

	@Override
	void setValue(A value);

}
