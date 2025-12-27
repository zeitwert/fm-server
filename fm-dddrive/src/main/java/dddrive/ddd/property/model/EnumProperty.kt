package dddrive.ddd.property.model

import dddrive.ddd.enums.model.Enumerated
import dddrive.ddd.enums.model.Enumeration

interface EnumProperty<E : Enumerated> : ReferenceProperty<E, String> {

	val enumeration: Enumeration<E>

}
