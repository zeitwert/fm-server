package io.dddrive.property.model

import io.dddrive.enums.model.Enumerated
import io.dddrive.enums.model.Enumeration

interface EnumProperty<E : Enumerated> : ReferenceProperty<E, String> {

	val enumeration: Enumeration<E>

}
