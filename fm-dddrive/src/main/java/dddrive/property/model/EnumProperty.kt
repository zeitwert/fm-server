package dddrive.property.model

import dddrive.ddd.model.Enumerated
import dddrive.ddd.model.Enumeration

interface EnumProperty<E : Enumerated> : ReferenceProperty<E, String> {

	val enumeration: Enumeration<E>

}
