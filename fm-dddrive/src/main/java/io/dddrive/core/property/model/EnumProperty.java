package io.dddrive.core.property.model;

import io.dddrive.core.enums.model.Enumerated;
import io.dddrive.core.enums.model.Enumeration;

public interface EnumProperty<E extends Enumerated> extends BaseProperty<E> {

	Enumeration<E> getEnumeration();

}
