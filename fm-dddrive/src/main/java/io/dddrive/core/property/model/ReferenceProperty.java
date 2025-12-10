package io.dddrive.core.property.model;

import io.dddrive.core.ddd.model.Aggregate;

public interface ReferenceProperty<A extends Aggregate> extends BaseProperty<A> {

	Object getId();

	void setId(Object id);

	BaseProperty<Object> getIdProperty();

	@Override
	A getValue();

	void setValue(A value);

}
