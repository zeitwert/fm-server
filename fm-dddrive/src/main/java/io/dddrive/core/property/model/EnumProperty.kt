package io.dddrive.core.property.model

import io.dddrive.core.enums.model.Enumerated
import io.dddrive.core.enums.model.Enumeration

interface EnumProperty<E : Enumerated> : ReferenceProperty<E, String> {

	val enumeration: Enumeration<E>

}
