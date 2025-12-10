package io.dddrive.core.property.model;

import io.dddrive.core.ddd.model.Part;

public interface PartResolver<P extends Part<?>> {
	P get(Integer id);
}
