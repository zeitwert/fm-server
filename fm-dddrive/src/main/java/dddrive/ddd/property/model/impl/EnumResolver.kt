package dddrive.ddd.property.model.impl

import dddrive.ddd.enums.model.Enumerated

fun interface EnumResolver<E : Enumerated> {

	fun get(id: String): E

}
