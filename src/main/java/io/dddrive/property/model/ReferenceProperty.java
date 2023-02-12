package io.dddrive.property.model;

import io.dddrive.ddd.model.Aggregate;

public interface ReferenceProperty<A extends Aggregate> extends SimpleProperty<A> {

	Integer getId();

	void setId(Integer id);

	@Override
	A getValue();

	@Override
	void setValue(A value);

}
