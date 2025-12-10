package io.dddrive.core.property.model;

import io.dddrive.core.ddd.model.Part;

public interface PartReferenceProperty<P extends Part<?>> extends BaseProperty<P> {

	Integer getId();

	void setId(Integer id);

	BaseProperty<Integer> getIdProperty();

	@Override
P getValue();

	void setValue(P value);

}
